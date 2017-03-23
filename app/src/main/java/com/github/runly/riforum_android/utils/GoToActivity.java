package com.github.runly.riforum_android.utils;

import android.app.Activity;
import android.content.Intent;

import com.github.runly.riforum_android.ui.activity.BaseActivity;

/**
 * Created by ranly on 17-2-14.
 */

public class GoToActivity {
	public static void goTo(Activity activity, Class<? extends BaseActivity> activityClass) {
		Intent intent = new Intent(activity, activityClass);
		activity.startActivity(intent);
	}
}
