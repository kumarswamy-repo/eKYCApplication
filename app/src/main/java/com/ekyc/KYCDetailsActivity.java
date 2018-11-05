package com.ekyc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * Created by kumara on 31/10/18.
 */

public class KYCDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("eKYC Details");
        boolean isMatched = getIntent().getBooleanExtra("is_matched",false);
        double confidence = getIntent().getDoubleExtra("confidence",0.0);
        String picPath = getIntent().getStringExtra("pic_path");

        ImageView imageView = findViewById(R.id.front);
        TextView matched = findViewById(R.id.matched);
        TextView confidenceTextView = findViewById(R.id.confidence);

        Glide.with(KYCDetailsActivity.this)
                .load(new File(picPath))
                .into(imageView);
        matched.setText(getResources().getString(R.string.matched,""+isMatched));
        confidenceTextView.setText(getResources().getString(R.string.confidence,""+confidence));
    }
}
