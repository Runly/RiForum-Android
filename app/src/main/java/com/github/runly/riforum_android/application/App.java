package com.github.runly.riforum_android.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.github.runly.richedittext.utils.ContextUtils;
import com.github.runly.richedittext.utils.DisplayUtils;
import com.github.runly.riforum_android.model.Plate;
import com.github.runly.riforum_android.model.User;

import java.util.List;

/**
 * Created by ranly on 17-2-7.
 */

public class App extends Application{
    private static App instance;
    private User user;
    private boolean isLogin = false;
    private List<Plate> plateList;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaymetrics);

        ContextUtils.setContext(this);
        DisplayUtils.init(this);

        Constants.SCREEN_WIDTH = displaymetrics.widthPixels;
        Constants.SCREEN_HEIGHT = displaymetrics.heightPixels;
        Constants.STATUS_HEIGHT = getStatusBarHeight();
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
        if (user != null) {
            this.user = user;
            this.isLogin = true;
            Log.e("is login", String.valueOf(this.isLogin));
        } else {
            this.user = null;
            this.isLogin = false;
            Log.e("is login", String.valueOf(this.isLogin));
        }
    }

    public List<Plate> getPlateList() {
        return plateList;
    }

    public void setPlateList(List<Plate> plateList) {
        this.plateList = plateList;
    }

    public boolean isLogin() {
        return isLogin;
    }
}
