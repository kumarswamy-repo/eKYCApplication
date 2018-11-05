package com.ekyc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumara on 31/10/18.
 */

public class FaceMatch {
    @SerializedName("matched")
    boolean isMatched;
    @SerializedName("confidence")
    double confidenceValue;

    public boolean isMatched() {
        return isMatched;
    }

    public double getConfidenceValue() {
        return confidenceValue;
    }
}
