package com.github.runly.riforum_android.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ranly on 17-2-13.
 */

public class RetrofitFactory {
    private static RetrofitFactory instance;
    private static final int DEFAULT_TIMEOUT = 10;
    private static final String baseUrl = "http://www.ranly.info:7732/";
    private Retrofit retrofit;
    private UserService userService;
    private QiuniuTokenService qiuniuTokenService;
    private EntryService entryService;
    private CommentService commentService;


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
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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

    public QiuniuTokenService getQiuniuTokenService() {
        if (null == qiuniuTokenService) {
            qiuniuTokenService = getRetrofit().create(QiuniuTokenService.class);
        }
        return qiuniuTokenService;
    }

    public EntryService getEntryService() {
        if (null == entryService) {
            entryService = getRetrofit().create(EntryService.class);
        }
        return entryService;
    }

    public CommentService getCommentService() {
        if (null == commentService) {
            commentService = getRetrofit().create(CommentService.class);
        }
        return commentService;
    }
}
