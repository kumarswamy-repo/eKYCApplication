package com.ekyc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumara on 31/10/18.
 */

public class KycRequest {
    @SerializedName("idcardType")
    String idCardType;
    @SerializedName("idcardFrontPic")
    String frontPicId = null;
    @SerializedName("idcardBackPic")
    String backPicId = null;
    @SerializedName("clientPic")
    String clientPicId;

    public KycRequest(String idCardType) {
        this.idCardType = idCardType;
    }

    public void setFrontPicId(String frontPicId) {
        this.frontPicId = frontPicId;
    }

    public void setBackPicId(String backPicId) {
        this.backPicId = backPicId;
    }

    public void setClientPicId(String clientPicId) {
        this.clientPicId = clientPicId;
    }
}
