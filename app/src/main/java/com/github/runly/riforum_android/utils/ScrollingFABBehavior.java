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
    private float topBarHeight;
    private float sizeHide, sizeShow;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
//        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
//                new int[]{android.R.attr.actionBarSize});
//        topBarHeight = styledAttributes.getDimension(0, 0);
//        styledAttributes.recycle();
        topBarHeight = UnitConvert.dipToPixels(context, Constants.MAIN_TOPBAR_HEIGHT);
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

//        if (dependency instanceof AppBarLayout) {
//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//            int fabBottomMargin = lp.bottomMargin;
//            int distanceToScroll = fab.getHeight() + fabBottomMargin;
//            float ratio = (dependency.getY() - Constants.STATUS_HEIGHT) / topBarHeight;
////            float ratio = (dependency.getY()) / topBarHeight;
//            fab.setTranslationY(-distanceToScroll * ratio);
//        }
        return true;
    }
}
