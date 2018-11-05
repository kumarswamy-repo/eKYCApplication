package com.ekyc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumara on 31/10/18.
 */

public class KycResponse {
    @SerializedName("faceMatch")
    FaceMatch faceMatch;
    @SerializedName("clientInfo")
    ClientInfo clientInfo;
    @SerializedName("message")
    String message;

    public FaceMatch getFaceMatch() {
        return faceMatch;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }
}
