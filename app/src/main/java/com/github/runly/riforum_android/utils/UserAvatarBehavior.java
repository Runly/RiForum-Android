package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by ranly on 17-2-28.
 */

public class UserAvatarBehavior extends CoordinatorLayout.Behavior<ImageView> {
    private float dependencyEndY, avatarWidth;


    public UserAvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        avatarWidth = UnitConvert.dp2Px(context, 72);
        dependencyEndY = UnitConvert.dp2Px(context, 174); // Log查看dependency.getY()的边界值后除以屏幕密度 = 190 dp
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        Log.e("Y", dependency.getY()+"");

        ViewGroup.LayoutParams params = child.getLayoutParams();
        params.width = (int) (avatarWidth + avatarWidth * dependency.getY() / dependencyEndY);
        params.height = (int) (avatarWidth + avatarWidth * dependency.getY() / dependencyEndY);
        child.setLayoutParams(params);
        return true;
    }
}
