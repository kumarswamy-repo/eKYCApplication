package com.ekyc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by kumara on 30/10/18.
 */

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener{

    int picType;
    int docType;
    String picPath;
    int picNumber;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        picType = getIntent().getIntExtra("pic_type",0);
        docType = getIntent().getIntExtra("doc_type",-1);
        picNumber = getIntent().getIntExtra("pic_number",1);
        picPath = getIntent().getStringExtra("pic_path");
        ImageView picture = findViewById(R.id.picture);
        TextView titleTextView = findViewById(R.id.title);
        TextView retakeTextView = findViewById(R.id.retake);
        TextView nextTextView = findViewById(R.id.next);
        retakeTextView.setOnClickListener(this);
        nextTextView.setOnClickListener(this);
        String docName;
        if(picType == 2)
            docName = "Aadhar";
        else if(picType == 1)
            docName = "Pan card";
        else
            docName = "Passport";
        if(picType == 5)
            titleTextView.setText(getResources().getString(R.string.review_selie_title));
        else if(picNumber == 1)
            titleTextView.setText(getResources().getString(R.string.review_title,"Front",docName));
        else
            titleTextView.setText(getResources().getString(R.string.review_title,"Back",docName));
        if(picType == 5 || (picNumber == 1 && picType == 1) || picNumber == 2)
            nextTextView.setText(getResources().getString(R.string.done));
        else
            nextTextView.setText(getResources().getString(R.string.next));
        retakeTextView.setOnClickListener(this);
        nextTextView.setOnClickListener(this);

        Glide.with(ReviewActivity.this)
                .load(new File(picPath))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(picture);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.retake:{
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("pic_type",picType);
                bundle.putInt("doc_type",docType);
                bundle.putInt("pic_number",picNumber);
                bundle.putString("pic_path",picPath);
                bundle.putBoolean("is_retake",true);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
                break;
            }
            case R.id.next:{
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("pic_type",picType);
                bundle.putInt("doc_type",docType);
                bundle.putInt("pic_number",picNumber);
                bundle.putString("pic_path",picPath);
                bundle.putBoolean("is_retake",false);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("pic_type",picType);
        bundle.putInt("doc_type",docType);
        bundle.putInt("pic_number",picNumber);
        bundle.putString("pic_path",picPath);
        bundle.putBoolean("is_retake",true);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
