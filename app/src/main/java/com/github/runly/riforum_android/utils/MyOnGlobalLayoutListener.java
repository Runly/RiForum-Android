package com.github.runly.riforum_android.utils;

/**
 * Created by ranly on 17-3-22.
 */

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.github.runly.riforum_android.interfaces.OnSoftKeyBoardShow;

/**
 * 判断软键盘是否弹出
 */
public class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
	private OnSoftKeyBoardShow onSoftKeyBoardShow;
	private View rootView;

	public MyOnGlobalLayoutListener(View rootView, OnSoftKeyBoardShow onSoftKeyBoardShow) {
		this.onSoftKeyBoardShow = onSoftKeyBoardShow;
		this.rootView = rootView;
	}

	@Override
	public void onGlobalLayout() {
		final Rect rect = new Rect();
		rootView.getWindowVisibleDisplayFrame(rect);
		final int screenHeight = rootView.getRootView().getHeight();
		final int heightDifference = screenHeight - rect.bottom;
		boolean visible = heightDifference > screenHeight / 3;

		if (onSoftKeyBoardShow != null)
			onSoftKeyBoardShow.hasShow(visible);
	}
}
