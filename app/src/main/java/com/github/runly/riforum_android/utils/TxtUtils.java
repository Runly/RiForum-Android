package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;

import java.lang.reflect.Field;

/**
 * Created by ranly on 17-2-17.
 */

public class TxtUtils {
    public static String getReadableTime(String dateline) {
        App app = App.getInstance();
        if ("0".equals(dateline)) {
            return app.getString(R.string.time_ago_just_now);
        }
        Long timeMillis = Long.parseLong(dateline);
        int seconds = (int) (System.currentTimeMillis() / 1000 - timeMillis/1000);
        if (seconds <= 0) {
            return String.format(app.getString(R.string.time_ago_just_now));
        }
        int minutes = seconds / 60;
        if (minutes < 1) {
            return seconds == 1 ? app.getString(R.string.time_ago_second) : String.format(app.getString(R.string.time_ago_seconds), seconds);
//            return seconds + "秒之前";
        }
        int hours = minutes / 60;
        if (hours < 1) {
            return minutes == 1 ? app.getString(R.string.time_ago_minute) : String.format(app.getString(R.string.time_ago_minutes), minutes);
//            return minutes + "分钟之前";
        }
        int days = hours / 24;
        if (days < 1) {
            return hours == 1 ? app.getString(R.string.time_ago_hour) : String.format(app.getString(R.string.time_ago_hours), hours);
//            return hours + "小时之前";
        }
        int weeks = days / 7;
        if (weeks < 1) {
            return days == 1 ? app.getString(R.string.time_ago_day) : String.format(app.getString(R.string.time_ago_days), days);
//            return days + "天之前";
        } else if (weeks < 5) {
            return weeks == 1 ? app.getString(R.string.time_ago_week) : String.format(app.getString(R.string.time_ago_weeks), weeks);
//            return weeks + "周之前";
        }
        int months = days / 30;
        if (months < 12) {
            return months == 1 ? app.getString(R.string.time_ago_month) : String.format(app.getString(R.string.time_ago_months), months);
//            return months + "月之前";
        }
        int years = days / 365;
        return years == 1 ? app.getString(R.string.time_ago_year) : String.format(app.getString(R.string.time_ago_years), years);
//        return years + "年之前";
    }

    public static String getPlateWithId(int id) {
        String plateName = null;
        switch(id){
            case 1:
                plateName = Constants.NEWS;
                break;
            case 2:
                plateName = Constants.MEDIA;
                break;
            case 3:
                plateName = Constants.TRAVEL_FOOD;
                break;
            case 4:
                plateName = Constants.GAME;
                break;
            case 5:
                plateName = Constants.DAILY_LIFE;
        }


        return plateName;
    }


    /**
     * 通过反射修改TabLayout Indicator的宽度（仅在Android 4.2及以上生效）
     */
    public static void setUpIndicatorWidth(Context context, TabLayout tabLayout) {
        Class<?> tabLayoutClass = tabLayout.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayoutClass.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        LinearLayout layout = null;
        try {
            if (tabStrip != null) {
                layout = (LinearLayout) tabStrip.get(tabLayout);
            }
            if (layout != null) {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    child.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.setMarginStart(UnitConvert.dipToPixels(context, 35));
                        params.setMarginEnd(UnitConvert.dipToPixels(context, 35));
                    }
                    child.setLayoutParams(params);
                    child.invalidate();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
