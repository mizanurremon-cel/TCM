package com.cel.tcm.API;

import com.airbnb.lottie.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .protocols(Util.immutableListOf(Protocol.HTTP_1_1))
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build();
////    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
////            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//
//    OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .protocols(Util.immutableList(Protocol.HTTP_1_1))
//            .connectTimeout(3000, TimeUnit.SECONDS)
//            .readTimeout(3000, TimeUnit.SECONDS)
//            .writeTimeout(3000, TimeUnit.SECONDS)
//            .addNetworkInterceptor(httpLoggingInterceptor)
//            .build();
    public static Retrofit getRetrofit(String BASEURL) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASEURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
