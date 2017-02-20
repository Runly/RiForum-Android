package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ranly on 17-2-7.
 */

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private float topBarHeight;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
//        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
//                new int[]{android.R.attr.actionBarSize});
//        topBarHeight = styledAttributes.getDimension(0, 0);
//        styledAttributes.recycle();
        topBarHeight = UnitConvert.dipToPixels(context, Constant.TOPBAR_HEIGHT);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ratio = (dependency.getY() - Constant.STATUS_HEIGHT) / topBarHeight;
//            float ratio = (dependency.getY()) / topBarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }
}
