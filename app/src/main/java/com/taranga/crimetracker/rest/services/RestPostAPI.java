package com.taranga.crimetracker.rest.services;

import com.taranga.crimetracker.model.CrimeDetailsModel;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface RestPostAPI {

    @Headers({"Content-Type: application/json; charset=UTF-8"})
    @POST("SvcAccount/RegisterUser")
    Observable<String> postCrimeDetailsToRx(@Body CrimeDetailsModel crimeDetailsModel);

  /*  @Headers({"Content-Type: application/json; charset=UTF-8"})
    @POST("Sowing/PostSowingDetail")
    Observable<ServerResponse> postSowingDetailRx(@Body SowingMasterModel sowingMasterModel);*/
}
