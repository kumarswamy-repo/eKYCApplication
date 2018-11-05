package com.ekyc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kumara on 29/10/18.
 */

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{
    String TAG = CameraActivity.class.getSimpleName();
    ImageView closeCameraImageView;
    ImageView takePictureImageView;
    TextView picTitleTextView;
    TextView picDescriptionTextView;
    ProgressDialog processingDialog;
    View cropView;
    Camera mCamera;
    CameraPreview mPreview;
    int x,y,w,h;
    int picType;
    int docType;
    String picPath;
    int picNumber;
    boolean isSuccess;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        picType = getIntent().getIntExtra("pic_type",0);
        docType = getIntent().getIntExtra("doc_type",-1);
        picPath = getIntent().getStringExtra("pic_path");
        picNumber = getIntent().getIntExtra("pic_number",0);
        String description = getIntent().getStringExtra("pic_description");
        picTitleTextView = findViewById(R.id.picture_tile);
        picDescriptionTextView = findViewById(R.id.picture_description);
        closeCameraImageView = findViewById(R.id.camera_close);
        closeCameraImageView.setOnClickListener(this);
        takePictureImageView = findViewById(R.id.take_picture);
        takePictureImageView.setOnClickListener(this);
        cropView = findViewById(R.id.crop_view);
        if(picType == 5){
            cropView.setVisibility(View.GONE);
            picTitleTextView.setVisibility(View.GONE);
            picDescriptionTextView.setVisibility(View.GONE);
        }else{
            if(description != null && !TextUtils.isEmpty(description))
                picDescriptionTextView.setText(description);
            else
                picDescriptionTextView.setText(R.string.default_description);

            String picFace;
            if (picNumber == 1)
                picFace = "Front";
            else
                picFace = "Back";

            if(picType == 2)
                picTitleTextView.setText(getResources().getString(R.string.pic_title,picFace,"Aadhar"));
            else if(picType == 1)
                picTitleTextView.setText(getResources().getString(R.string.pic_title,picFace,"PAN card"));
            else
                picTitleTextView.setText(getResources().getString(R.string.pic_title,picFace,"Passport"));


            cropView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    cropView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int[] location = new int[2];
                    cropView.getLocationOnScreen(location);
                    x = location[0];
                    y = location[1];
                    Log.e("X,Y==",location[0] +" "+location[1]);
                    w = cropView.getMeasuredWidth();
                    h = cropView.getMeasuredHeight();

                    Log.e("measure WxH=",w +"x"+h);
                    Log.e("measure WxH=",cropView.getWidth() +"x"+cropView.getHeight());

                }
            });
        }

        // Create an instance of Camera
        mCamera = getCameraInstance();

        if(mCamera == null){
            Toast.makeText(CameraActivity.this,"Unable to access camera. Please try again",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            // Create our Preview view and set it as the content of our activity.
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(parameters);
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = findViewById(R.id.preview);
            preview.addView(mPreview);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private Camera getCameraInstance(){
        int cameraCount = 0;
        Camera camera = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraInfo.orientation =
        cameraCount = Camera.getNumberOfCameras();
        int facing;
        if(picType == 5)
            facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        else
            facing = Camera.CameraInfo.CAMERA_FACING_BACK;

        for (int cameraId=0;cameraId<cameraCount;cameraId++){
            Camera.getCameraInfo(cameraId,cameraInfo);
            if (cameraInfo.facing == facing) {
                try {
                    camera = Camera.open(cameraId);
                    camera.setDisplayOrientation(90);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }

                if(camera!=null)
                    break;
            }
        }
        return camera;
    }

    private void releaseCamera(){
        if(mCamera!=null)
            mCamera.release();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            mCamera.stopPreview();
            processingDialog = ProgressDialog.show(CameraActivity.this,null,"Processing...",true,false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(picType == 5){
                        saveSelfiePicture(data);
                    }else{
                        saveCroppedPicture(data);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            releaseCamera();
                            if(processingDialog != null && processingDialog.isShowing())
                                processingDialog.dismiss();
                            if(isSuccess){
                                Intent resultIntent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putInt("pic_type",picType);
                                bundle.putInt("doc_type",docType);
                                bundle.putString("pic_path",picPath);
                                bundle.putInt("pic_number",picNumber);
                                resultIntent.putExtras(bundle);
                                setResult(RESULT_OK,resultIntent);
                            }else {
                                setResult(RESULT_CANCELED);
                            }
                            finish();
                        }
                    });
                }
            }).start();

        }
    };

    private void saveCroppedPicture(byte[] data){
        Bitmap srcBitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        srcBitmap = Bitmap.createBitmap(srcBitmap,0,0,srcBitmap.getWidth(),srcBitmap.getHeight(),matrix,true);

        Log.e("bit map WxH:",srcBitmap.getWidth()+" "+srcBitmap.getHeight());
        int height = srcBitmap.getHeight();
        Bitmap croppedBitmap = Bitmap.createBitmap(srcBitmap,0,height/4,srcBitmap.getWidth(),height/2);

        File dest = new File(picPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dest);
            // TODO taking time to write bitmap into file. Needs to check
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            isSuccess = true;
        } catch (FileNotFoundException e) {
            isSuccess = false;
            Log.e(TAG, "File not found: " + e.getMessage());
        } finally {
            try {
                if(fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSelfiePicture(byte[] data){
        Bitmap srcBitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        srcBitmap = Bitmap.createBitmap(srcBitmap,0,0,srcBitmap.getWidth(),srcBitmap.getHeight(),matrix,true);
        File dest = new File(picPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dest);
            srcBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            isSuccess = true;
        } catch (FileNotFoundException e) {
            isSuccess = false;
            Log.e(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            try {
                if(fos != null) {
                    fos.flush();
                    fos.close();
                }
//                setOrientationForSelfie(dest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setOrientationForSelfie(String picPath){
        try {
            ExifInterface ei = new ExifInterface(picPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            File dest = new File(picPath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(dest);
                // TODO taking time to write bitmap into file. Needs to check
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,50,fos);
                isSuccess = true;
            } catch (FileNotFoundException e) {
                isSuccess = false;
                Log.e(TAG, "File not found: " + e.getMessage());
            } finally {
                try {
                    if(fos != null) {
                        fos.flush();
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.camera_close:{
                releaseCamera();
                finish();
                break;
            }
            case R.id.take_picture:{
                mCamera.takePicture(null,null,mPicture);
                break;
            }
        }
    }
}
