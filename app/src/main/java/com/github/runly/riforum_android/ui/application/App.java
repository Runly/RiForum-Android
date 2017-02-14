package com.github.runly.riforum_android.ui.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.github.runly.richedittext.utils.ContextUtils;
import com.github.runly.richedittext.utils.DisplayUtils;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.utils.Constant;
import com.github.runly.riforum_android.utils.SdCardUtil;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-7.
 */

public class App extends Application{
    private static App instance;
    private User user;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // 异步读取存储在Sdcard上的User对象
        Observable.just(instance)
                .map(SdCardUtil::loadUserFromSdCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setUser);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaymetrics);

        ContextUtils.setContext(this);
        DisplayUtils.init(this);

        Constant.SCREEN_WIDTH = displaymetrics.widthPixels;
        Constant.SCREEN_HEIGHT = displaymetrics.heightPixels;
        Constant.STATUS_HEIGHT = getStatusBarHeight();
    }

    private int getStatusBarHeight() {
        int statusBarHeight1 = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
