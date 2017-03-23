package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.runly.riforum_android.application.Constants;

/**
 * Created by ranly on 17-3-9.
 */

public class BitmapUtil {

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int width = options.outWidth;
		final int height = options.outHeight;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			//使用需要的宽高的最大值来计算比率
			final int suitedValue = reqHeight > reqWidth ? reqHeight : reqWidth;
			final int heightRatio = Math.round((float) height / (float) suitedValue);
			final int widthRatio = Math.round((float) width / (float) suitedValue);

			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;//用最大
		}
		return inSampleSize;
	}

	public static Bitmap createScaledBitmap(Context context, int resID) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resID, options);
		options.inSampleSize = calculateInSampleSize(options, Constants.SCREEN_WIDTH, UnitConvert.dp2Px(context, 250));
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(context.getResources(), resID, options);
	}
}
