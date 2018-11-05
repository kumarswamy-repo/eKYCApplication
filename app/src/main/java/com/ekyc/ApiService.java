package com.ekyc;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by kumara on 30/10/18.
 */

public interface ApiService {
    @POST("imageupload")
    Observable<UploadResponse> uploadImage(@Header("Content-Type") String contentType,@Body UploadRequest uploadRequest);

    @POST("ekyc")
    Observable<KycResponse> getKYCResponse(@Header("Content-Type") String contentType, @Body KycRequest kycRequest);
}
