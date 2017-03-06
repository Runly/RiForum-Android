package com.github.runly.riforum_android.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.utils.SharedPreferencesUtil;

/**
 * Created by ranly on 17-2-8.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setMode();
        super.onCreate(savedInstanceState);
    }

    private void setMode() {
        int mode = SharedPreferencesUtil.getInt(Constants.WHICH_MODE);
        if (mode == Constants.DAY_MODE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
