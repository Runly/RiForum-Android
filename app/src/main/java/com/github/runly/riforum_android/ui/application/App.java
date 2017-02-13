package com.github.runly.riforum_android.ui.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.github.runly.richedittext.utils.ContextUtils;
import com.github.runly.richedittext.utils.DisplayUtils;
import com.github.runly.riforum_android.utils.Constant;

/**
 * Created by ranly on 17-2-7.
 */

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
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
}
