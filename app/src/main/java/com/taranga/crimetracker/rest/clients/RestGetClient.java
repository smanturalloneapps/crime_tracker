package com.taranga.crimetracker.rest.clients;

import android.content.Context;

import com.taranga.crimetracker.helper.ServerConstants;
import com.taranga.crimetracker.rest.interceptor.RequestInterceptor;
import com.taranga.crimetracker.rest.services.RestGetAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestGetClient {

    private RestGetAPI restGetAPI;

    public RestGetClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor(context))
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.getMainUrlToTrackCrimes())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        restGetAPI = retrofit.create(RestGetAPI.class);
    }

    public RestGetAPI getInstance(Context c) {
        new RestGetClient(c);
        return restGetAPI;
    }
}