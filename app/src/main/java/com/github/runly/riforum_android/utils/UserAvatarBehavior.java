package com.github.runly.riforum_android.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by ranly on 17-2-28.
 */

public class UserAvatarBehavior extends CoordinatorLayout.Behavior<ImageView> {
//    private static final int avatarMarginTop = 75;

//    private Context mContext;
    private float dependencyEndY, avatarWidth;
//    private float translationY;

    public UserAvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
//        this.mContext = context;


//        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
//                new int[]{android.R.attr.actionBarSize});
//        float topBarHeight = styledAttributes.getDimension(0, 0);
//        styledAttributes.recycle();

//        translationY = UnitConvert.dipToPixels(mContext, avatarMarginTop) -
//                (topBarHeight) / 2 -
//                Constants.STATUS_HEIGHT;
        avatarWidth = UnitConvert.dipToPixels(context, 72);
        dependencyEndY = UnitConvert.dipToPixels(context, 174); // Log查看dependency.getY()的边界值后除以屏幕密度 = 190 dp
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
//        Log.e("Y", dependency.getY()+"");
//        float y = dependency.getY() / dependencyEndY * translationY / 8;
//        child.setTranslationY(y);

        ViewGroup.LayoutParams params = child.getLayoutParams();
        params.width = (int) (avatarWidth + avatarWidth * dependency.getY() / dependencyEndY);
        params.height = (int) (avatarWidth + avatarWidth * dependency.getY() / dependencyEndY);
        child.setLayoutParams(params);
        return true;
    }
}
