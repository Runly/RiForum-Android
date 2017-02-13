package com.github.runly.riforum_android.retrofit;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ranly on 17-2-13.
 */

public class RetrofitFactory {
    public static RetrofitFactory instance;
    private static final int DEFAULT_TIMEOUT = 10;
    private static final String baseUrl = "http://www.ranly.top:5000/";
    private Retrofit retrofit;
    private UserService userService;


    public static RetrofitFactory getInstance() {
        if (null == instance) {
            instance = new RetrofitFactory();
        }
        return instance;
    }

    private Retrofit getRetrofit() {
        if (null == retrofit) {
            //手动创建一个OkHttpClient并设置超时时间
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build();
        }

        return retrofit;
    }

    public UserService getUserService() {
        if (null == userService) {
            userService = getRetrofit().create(UserService.class);
        }
        return userService;
    }

}
