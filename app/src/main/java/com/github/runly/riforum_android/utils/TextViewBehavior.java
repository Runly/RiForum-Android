package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.runly.riforum_android.R;

import static android.R.attr.translationY;
import static android.R.attr.y;

/**
 * Created by ranly on 17-2-28.
 */

public class TextViewBehavior extends CoordinatorLayout.Behavior<TextView>  {


    private Context mContext;

    private int dependencyEndY;

    public TextViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        dependencyEndY = UnitConvert.dipToPixels(context, 174); // Log查看dependency.getY()的边界值后除以屏幕密度 = 166 dp
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        Log.e("Y", dependency.getY()+"");
        if (-dependency.getY() == dependencyEndY) {
            child.setTextColor(Color.WHITE);
            child.setBackground(ContextCompat.getDrawable(mContext, R.drawable.color_white_text_border));
        } else {
            child.setTextColor(ContextCompat.getColor(mContext, R.color.colorBase));
            child.setBackground(ContextCompat.getDrawable(mContext, R.drawable.color_base_text_border));
        }

        return true;
    }
}
