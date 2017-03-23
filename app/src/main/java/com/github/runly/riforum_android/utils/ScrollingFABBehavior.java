package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.github.runly.riforum_android.application.Constants;

/**
 * Created by ranly on 17-2-7.
 */

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
	private float sizeHide, sizeShow;

	public ScrollingFABBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);

		float topBarHeight = UnitConvert.dp2Px(context, Constants.MAIN_TOPBAR_HEIGHT);
		sizeHide = -topBarHeight;
		sizeShow = 0;
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
		return dependency instanceof AppBarLayout;
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
		if (dependency.getY() == sizeHide) {
			fab.hide();
		}

		if (dependency.getY() == sizeShow) {
			fab.show();
		}

		return true;
	}
}
