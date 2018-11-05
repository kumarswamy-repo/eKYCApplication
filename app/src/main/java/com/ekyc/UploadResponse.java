package com.ekyc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumara on 30/10/18.
 */

public class UploadResponse {
    @SerializedName("imageId")
    String imageId;

    public String getImageId() {
        return imageId;
    }
}
