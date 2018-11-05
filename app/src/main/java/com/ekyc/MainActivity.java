package com.ekyc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton fab;
    FloatingActionButton fab_passport;
    FloatingActionButton fab_pancard;
    FloatingActionButton fab_aadhar;
    TextView textView_passport;
    TextView textView_pancard;
    TextView textView_addhar;
    RelativeLayout snackbarLayout;
    Snackbar snackbar;
    int REQ_CODE_PERMISSIONS = 100;
    final int REQ_CODE_CAMERA = 101;
    final int REQ_CODE_REVIEW = 102;
    boolean isFABMenuShown;
    Map<String,File> filesToUpload;
    Retrofit retrofit;
    SharedPreferences sharedPreferences;
    ProgressDialog uploadingDialog;
    ProgressDialog gettingKYCDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(Utility.PREF_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab_passport = findViewById(R.id.fab_passport);
        fab_passport.setOnClickListener(this);
        fab_pancard = findViewById(R.id.fab_pancard);
        fab_pancard.setOnClickListener(this);
        fab_aadhar = findViewById(R.id.fab_aadhar);
        fab_aadhar.setOnClickListener(this);
        textView_passport = findViewById(R.id.passport);
        textView_pancard = findViewById(R.id.pancard);
        textView_addhar = findViewById(R.id.aadhar);
        snackbarLayout = findViewById(R.id.snackbar_layout);
//        Button upload = findViewById(R.id.button);
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadPicsToServer(0);
//            }
//        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:{
                if (snackbar != null)
                    snackbar.dismiss();
                if(!isFABMenuShown)
                    checkForPermissions();
                else
                    hideFABMenu();
                break;
            }
            case R.id.fab_passport:{
                hideFABMenu();
                Bundle bundle = new Bundle();
                bundle.putInt("pic_type",0);
                bundle.putString("pic_path",Utility.getPassportFrontSavingPath());
                bundle.putString("pic_description","Place Passport page inside the box");
                bundle.putInt("pic_number",1);
                launchCameraActivity(bundle);
                break;
            }
            case R.id.fab_pancard:{
                hideFABMenu();
                Bundle bundle = new Bundle();
                bundle.putInt("pic_type",1);
                bundle.putString("pic_path",Utility.getPanCardSavingPath());
                bundle.putString("pic_description","Place PAN card page inside the box");
                bundle.putInt("pic_number",1);
                launchCameraActivity(bundle);
                break;
            }
            case R.id.fab_aadhar:{
                hideFABMenu();
                Bundle bundle = new Bundle();
                bundle.putInt("pic_type",2);
                bundle.putString("pic_path",Utility.getAadharFrontSavingPath());
                bundle.putString("pic_description","Place Aadhar page inside the box");
                bundle.putInt("pic_number",1);
                launchCameraActivity(bundle);
                break;
            }
        }
    }

    private void checkForPermissions(){
        List<String> requiredPermissionsList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requiredPermissionsList.add(Manifest.permission.CAMERA);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requiredPermissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            requiredPermissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(requiredPermissionsList.size() > 0){
            String[] reuiredPermissions = new String[requiredPermissionsList.size()];
            ActivityCompat.requestPermissions(this,requiredPermissionsList.toArray(reuiredPermissions),REQ_CODE_PERMISSIONS);
        }else {
            showFABMenu();
//            launchCameraActivity();
        }
    }

    private void showFABMenu(){
        fab_passport.setVisibility(View.VISIBLE);
        textView_passport.setVisibility(View.VISIBLE);
        fab_pancard.setVisibility(View.VISIBLE);
        textView_pancard.setVisibility(View.VISIBLE);
        fab_aadhar.setVisibility(View.VISIBLE);
        textView_addhar.setVisibility(View.VISIBLE);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_white));
        isFABMenuShown = true;
    }

    private void hideFABMenu(){
        fab_passport.setVisibility(View.GONE);
        textView_passport.setVisibility(View.GONE);
        fab_pancard.setVisibility(View.GONE);
        textView_pancard.setVisibility(View.GONE);
        fab_aadhar.setVisibility(View.GONE);
        textView_addhar.setVisibility(View.GONE);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera));
        isFABMenuShown = false;
    }

    private void launchCameraActivity(Bundle bundle){
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent,REQ_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE_PERMISSIONS){
            List<String> permissionsDeniedList = new ArrayList<>();
            for (int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    permissionsDeniedList.add(permissions[i]);
            }

            if(permissionsDeniedList.size() > 0){
                List<String> neverAskPermissionsList = new ArrayList<>();
                List<String> requestPermissionsList = new ArrayList<>();
                for(String permission: permissionsDeniedList){
                    if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permission)){
                        neverAskPermissionsList.add(permission);
                    }else{
                        requestPermissionsList.add(permission);
                    }
                }
                if(requestPermissionsList.size() > 0){
                    List<String> toRequestPermission = new ArrayList<>();
                    for(int index = 0; index < requestPermissionsList.size() ; index++){
                        String permission = requestPermissionsList.get(index);
                        switch (permission) {
                            case Manifest.permission.CAMERA:
                                toRequestPermission.add("Camera");
                                break;
                            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                toRequestPermission.add("Storage");
                                break;
                        }
                    }
                    String[] permissionsArray = new String[toRequestPermission.size()];
                    toRequestPermission.toArray(permissionsArray);
                    showRequiredPermissionsSnackbar(Arrays.toString(permissionsArray));
                }
                else if(neverAskPermissionsList.size()  > 0){
                    String[] toShowSettingsPermission = new String[neverAskPermissionsList.size()];
                    for(int index = 0; index < neverAskPermissionsList.size() ; index++){
                        String permission = neverAskPermissionsList.get(index);
                        switch (permission) {
                            case Manifest.permission.CAMERA:
                                toShowSettingsPermission[index] = "Camera";
                                break;
                            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                toShowSettingsPermission[index] = "Storage";
                                break;
                        }
                    }
                    showPermissionsRationale(Arrays.toString(toShowSettingsPermission));
                }
            }
            else{
                showFABMenu();
//                launchCameraActivity();
            }
        }
    }

    private void uploadPicsToServer(final int picType) {
        File picsSavedDir = Utility.getPicSavedDir(picType);
        filesToUpload = new HashMap<>();
        if (picsSavedDir != null) {
            if (new File(picsSavedDir, "front.jpg").exists())
                filesToUpload.put("front", new File(picsSavedDir, "front.jpg"));
            if (new File(picsSavedDir, "back.jpg").exists())
                filesToUpload.put("back", new File(picsSavedDir, "back.jpg"));
            if (new File(picsSavedDir, "selfie.jpg").exists())
                filesToUpload.put("selfie", new File(picsSavedDir, "selfie.jpg"));
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("doc_type", picType);
        editor.apply();
        uploadingDialog = ProgressDialog.show(MainActivity.this,null,"Uploading Pics to Server...",true,false);
        uploadNextPicture("front");

    }

    private void uploadNextPicture(final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final File fileToUpload;
                if (filesToUpload.containsKey(key))
                    fileToUpload = filesToUpload.get(key);
                else {
                    if (key.equals("front")) {
                        uploadNextPicture("back");
                        return;
                    } else if (key.equals("back")) {
                        uploadNextPicture("selfie");
                        return;
                    } else {
                        return;
                    }
                }
                ApiService apiService = getRetrofit().create(ApiService.class);
                String encodedImage = "data:image/jpg;base64," + convertToBase64(fileToUpload.getAbsolutePath(), true);
                final UploadRequest uploadRequest = new UploadRequest(encodedImage);
                apiService.uploadImage("application/json",uploadRequest)
                        .take(1)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<UploadResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(UploadResponse uploadResponse) {
                                if (uploadResponse != null) {
                                    String imageId = uploadResponse.getImageId();
                                    if (key.equals("front")) {
                                        Log.e("front_id",imageId);
                                        filesToUpload.remove("front");
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("front_id", imageId);
                                        editor.apply();
                                        uploadNextPicture("back");
                                    } else if (key.equals("back")) {
                                        Log.e("back_id",imageId);
                                        filesToUpload.remove("back");
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("back_id", imageId);
                                        editor.apply();
                                        uploadNextPicture("selfie");
                                    } else {
                                        Log.e("selfie_id",imageId);
                                        filesToUpload.clear();
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("selfie_id", imageId);
                                        editor.apply();
                                        if(uploadingDialog!=null && uploadingDialog.isShowing())
                                            uploadingDialog.dismiss();
                                        getKYCResponse();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to upload. Try Again.", Toast.LENGTH_SHORT).show();
                                    filesToUpload.clear();
                                    if(uploadingDialog!=null && uploadingDialog.isShowing())
                                        uploadingDialog.dismiss();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Failed to upload. Try Again.", Toast.LENGTH_SHORT).show();
                                filesToUpload.clear();
                                if(uploadingDialog!=null && uploadingDialog.isShowing())
                                    uploadingDialog.dismiss();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }).start();

    }

    private void getKYCResponse(){
        gettingKYCDialog = ProgressDialog.show(MainActivity.this,null,"Getting eKYC response from Server...",true,false);

        final int docType = sharedPreferences.getInt("doc_type",-1);
        String frontPicId = sharedPreferences.getString("front_id",null);
        String backPicId = sharedPreferences.getString("back_id",null);
        String clientPicId = sharedPreferences.getString("selfie_id",null);

        String docName = "";
        if(docType == 2)
            docName = "aadhar";
        else if(docType == 1)
            docName = "pan";
        else if(docType == 0)
            docName = "passport";

        KycRequest kycRequest = new KycRequest(docName);
        kycRequest.setFrontPicId(frontPicId);
        kycRequest.setBackPicId(backPicId);
        kycRequest.setClientPicId(clientPicId);

        ApiService apiService = getRetrofit().create(ApiService.class);
        apiService.getKYCResponse("application/json",kycRequest)
                .take(1)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<KycResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KycResponse kycResponse) {
                        if(gettingKYCDialog != null && gettingKYCDialog.isShowing())
                            gettingKYCDialog.dismiss();
                        if(kycResponse != null){
                            FaceMatch faceMatch = kycResponse.getFaceMatch();
                            boolean isMatched = faceMatch.isMatched();
                            double confidence = faceMatch.getConfidenceValue();
                            Intent intent = new Intent(MainActivity.this,KYCDetailsActivity.class);
                            intent.putExtra("is_matched",isMatched);
                            intent.putExtra("confidence",confidence);
                            if(docType == 2)
                                intent.putExtra("pic_path",Utility.getAadharFrontSavingPath());
                            else if(docType == 1)
                                intent.putExtra("pic_path",Utility.getPanCardSavingPath());
                            else  if(docType == 0)
                                intent.putExtra("pic_path",Utility.getPassportFrontSavingPath());
                            else
                                intent.putExtra("pic_path","");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(gettingKYCDialog != null && gettingKYCDialog.isShowing())
                            gettingKYCDialog.dismiss();
                        Toast.makeText(MainActivity.this, "failed to get eKYC response. Please try again",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    private void showPermissionsRationale(String permissions){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Need Storage Permission");
        builder.setMessage("eKYC requires "+permissions +" permissions to continue. Please grant in settings");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void showRequiredPermissionsSnackbar(String message){
        snackbar = Snackbar
                .make(snackbarLayout,"Please grant "+message+" permissions to continue.", Snackbar.LENGTH_LONG)
                .setAction("GRANT", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkForPermissions();
                    }
                });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_CAMERA) {
            if(resultCode == RESULT_OK){
                if(data != null){
                    int picType = data.getIntExtra("pic_type",0);
                    String picPath = data.getStringExtra("pic_path");
                    int picNumber = data.getIntExtra("pic_number",1);
                    int docType = data.getIntExtra("doc_type",-1);
                    Bundle bundle = new Bundle();
                    bundle.putInt("pic_type",picType);
                    bundle.putInt("doc_type",docType);
                    bundle.putString("pic_path",picPath);
                    bundle.putInt("pic_number",picNumber);
                    Intent intent = new Intent(MainActivity.this,ReviewActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,REQ_CODE_REVIEW);
                }
            }
        }else if(requestCode == REQ_CODE_REVIEW){
            if(resultCode == RESULT_OK){
                if(data != null){
                    int picType = data.getIntExtra("pic_type",0);
                    String picPath = data.getStringExtra("pic_path");
                    int picNumber = data.getIntExtra("pic_number",1);
                    boolean isRetake = data.getBooleanExtra("is_retake",false);
                    int docType = data.getIntExtra("doc_type",-1);
                    if(isRetake){
                        File picPathFile = new File(picPath);
                        picPathFile.delete();
                        Bundle bundle = new Bundle();
                        bundle.putInt("pic_type",picType);
                        bundle.putInt("pic_number",picNumber);
                        bundle.putString("pic_path",picPath);
                        bundle.putInt("doc_type",docType);
                        String description = "";
                        switch (picType){
                            case 2:{
                                description = "Place Aadhar page inside the box";
                                break;
                            }
                            case 1:{
                                description = "Place Pan Card page inside the box";
                                break;
                            }
                            case 0:{
                                description = "Place Passport inside the box";
                                break;
                            }
                        }
                        bundle.putString("pic_description",description);
                        Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent,REQ_CODE_CAMERA);

                    }else{
                        Bundle bundle = new Bundle();
                        switch (picType){
                            case 5:{
                                uploadPicsToServer(docType);
                                break;
                            }
                            case 2:{
                                if(picNumber == 2){
                                    bundle.putInt("pic_type",5);
                                    bundle.putInt("pic_number",1);
                                    bundle.putInt("doc_type",picType);
                                    bundle.putString("pic_path",Utility.getSelfieSavingPath(picType));
                                    bundle.putString("pic_description","");
                                    launchCameraActivity(bundle);
                                }else{
                                    bundle.putInt("pic_type",picType);
                                    bundle.putInt("pic_number",2);
                                    bundle.putString("pic_path",Utility.getAadharBackSavingPath());
                                    bundle.putString("pic_description","Place Aadhar page inside the box");
                                    launchCameraActivity(bundle);
                                }
                                break;
                            }
                            case 1:{
                                if(picNumber == 1){
                                    bundle.putInt("pic_type",5);
                                    bundle.putInt("pic_number",1);
                                    bundle.putInt("doc_type",picType);
                                    bundle.putString("pic_path",Utility.getSelfieSavingPath(picType));
                                    bundle.putString("pic_description","");
                                    launchCameraActivity(bundle);

                                }else{
                                    bundle.putInt("pic_type",picType);
                                    bundle.putInt("pic_number",1);
                                    bundle.putString("pic_path",Utility.getPanCardSavingPath());
                                    bundle.putString("pic_description","Place Pan Card page inside the box");
                                    launchCameraActivity(bundle);
                                }
                                break;
                            }
                            case 0:{
                                if(picNumber == 2){
                                    bundle.putInt("pic_type",5);
                                    bundle.putInt("pic_number",1);
                                    bundle.putInt("doc_type",picType);
                                    bundle.putString("pic_path",Utility.getSelfieSavingPath(picType));
                                    bundle.putString("pic_description","");
                                    launchCameraActivity(bundle);
                                }else{
                                    bundle.putInt("pic_type",picType);
                                    bundle.putInt("pic_number",2);
                                    bundle.putString("pic_path",Utility.getPassportBackSavingPath());
                                    bundle.putString("pic_description","Place Passport page inside the box");
                                    launchCameraActivity(bundle);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Retrofit getRetrofit(){
        if(retrofit == null){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(loggingInterceptor);
            retrofit = new Retrofit.Builder()
                    .baseUrl(Utility.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    private String convertToBase64(String imagePath,boolean toCompress)
    {
        String result = "";
        try {
            if(toCompress) {
                ExifInterface exif = new ExifInterface(imagePath);
                byte[] bytes = exif.getThumbnail();
                if (bytes == null)
                    bytes = compressImage(imagePath,30);
                if (bytes != null)
                    result = Base64.encodeToString(bytes, Base64.DEFAULT);
            }
            else {
                byte[] bytes =  compressImage(imagePath,100);
                if (bytes != null)
                    result = Base64.encodeToString(bytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    private byte[] compressImage(String imagePath,int quality) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

}
