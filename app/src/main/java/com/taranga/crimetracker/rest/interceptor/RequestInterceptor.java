package com.taranga.crimetracker.rest.interceptor;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {

    private final Context context;

    public RequestInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .build();
        return chain.proceed(request);
    }
}


// .addHeader("Authorization", "Bearer " + Utils.getStringDataFromSP(context, AppConstants.ACCESS_TOKEN))