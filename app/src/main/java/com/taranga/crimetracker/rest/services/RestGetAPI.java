package com.taranga.crimetracker.rest.services;

import com.taranga.crimetracker.helper.ServerConstants;

import retrofit2.http.GET;
import rx.Observable;

public interface RestGetAPI {

    // MAIN_URL_LIVE_ID
    // TEST_URL_BANGALORE_ID
    // TEST_URL_TUMKUR_ID

    @GET(ServerConstants.MAIN_URL_LIVE_ID)
    Observable<RestService> getCrimeDetailsFromRx();
}
