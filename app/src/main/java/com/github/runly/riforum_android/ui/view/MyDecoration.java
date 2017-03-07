package com.github.runly.riforum_android.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.utils.UnitConvert;

/**
 * Created by ranly on 17-3-6.
 */

public class MyDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Paint mPaint;
//    private Drawable mDivider;
    private int height;



    public MyDecoration(Context context, int height) {
        this.mContext = context;
//        this.mDivider = ContextCompat.getDrawable(context, R.drawable.recycler_divider);
        this.height = UnitConvert.dipToPixels(context, height);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(context, R.color.item_dividing));
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontalLine(c, parent);
    }

    //画横线, 这里的parent其实是显示在屏幕显示的这部分
    private void drawHorizontalLine(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
//            if (i == 0) {
//                continue;
//            }
            if (i == childCount-1) {
                continue;
            }
            final View child = parent.getChildAt(i);
            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + height;
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
            if (mPaint != null) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //画横线，就是往下偏移一个分割线的高度
        outRect.set(0, 0, 0, height);

    }
}
