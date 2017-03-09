package com.github.runly.riforum_android.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.runly.riforum_android.utils.UnitConvert;

/**
 * Created by ranly on 17-3-6.
 */

public class MyDecoration extends RecyclerView.ItemDecoration {
    private int left, top, right, bottom;
    private boolean drawFirst = true;

    public MyDecoration(Context context, int left, int top, int right, int bottom, boolean drawFirst) {
        this.left = UnitConvert.dp2Px(context, left);
        this.top = UnitConvert.dp2Px(context, top);
        this.right = UnitConvert.dp2Px(context, right);
        this.bottom = UnitConvert.dp2Px(context, bottom);
        this.drawFirst = drawFirst;
    }

    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int currentIndex = parent.getChildLayoutPosition(view);
        final int lastPosition = state.getItemCount() - 1;
        if (!drawFirst && currentIndex == 0) {
            outRect.set(0, 0, 0, 0);
        } else if (currentIndex == lastPosition) {
            outRect.set(left, top, right, top);
        } else {
            outRect.set(left, top, right, bottom);
        }

    }
}
