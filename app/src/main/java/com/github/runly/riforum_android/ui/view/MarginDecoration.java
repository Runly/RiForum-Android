package com.github.runly.riforum_android.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.runly.riforum_android.utils.UnitConvert;

/**
 * Created by ranly on 17-3-8.
 */

public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int left, top, right, bottom, biggerSize;

    public MarginDecoration(Context context, int left, int top, int right, int bottom) {
        this.left = UnitConvert.dp2Px(context, left);
        this.top = UnitConvert.dp2Px(context, top);
        this.right = UnitConvert.dp2Px(context, right);
        this.bottom = UnitConvert.dp2Px(context, bottom);
        this.biggerSize = UnitConvert.dp2Px(context, 8);
    }

    @Override
    public void getItemOffsets(
        Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int currentIndex = parent.getChildLayoutPosition(view);
        final int lastPosition = state.getItemCount() - 1;
        boolean isEnd;
        if(currentIndex == 0) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        if (lastPosition % 2 == 0) {
            isEnd = currentIndex == lastPosition || currentIndex == lastPosition - 1;
        }else {
            isEnd = currentIndex == lastPosition;
        }

        if (currentIndex % 2 == 1) {
            if (isEnd) {
                outRect.set(biggerSize, top, right, bottom);
            } else {
                outRect.set(biggerSize, top, right, 0);
            }
        } else {
            if (isEnd) {
                outRect.set(left, top, biggerSize, bottom);
            } else {
                outRect.set(left, top, biggerSize, 0);
            }
        }
    }
}
