package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ranly on 17-2-13.
 */

public class ToastUtil {
	public static void makeShortToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void makeLongToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
}
