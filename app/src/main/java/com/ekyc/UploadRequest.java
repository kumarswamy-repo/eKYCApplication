package com.ekyc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumara on 30/10/18.
 */

public class UploadRequest {
    @SerializedName("image")
    String image;

    public UploadRequest(String image) {
        this.image = image;
    }
}
