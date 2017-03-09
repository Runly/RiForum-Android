package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.runly.riforum_android.R;

/**
 * Created by ranly on 17-2-28.
 */

public class ImageViewBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private Context mContext;

    private int dependencyEndY;

    public ImageViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        dependencyEndY = UnitConvert.dp2Px(context, 174); // Log查看dependency.getY()的边界值后除以屏幕密度 = 166 dp
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        Log.e("Y", dependency.getY()+"");
        if (-dependency.getY() == dependencyEndY) {
            child.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.back));
        } else {
            child.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.back_base_color));
        }

        return true;
    }
}
