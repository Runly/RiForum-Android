package com.github.runly.riforum_android.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.utils.PlateHeaderNumUtil;
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
		int currentPosition = parent.getChildLayoutPosition(view);
		// 为了保持原来item的position的奇偶性不变，要减去当前位置之前的PlateHeader的数量
		currentPosition -= PlateHeaderNumUtil.getPlateHeaderNumber(currentPosition);
		final int lastPosition = state.getItemCount() - Constants.PLATE_COUNT - 1;
		boolean isEnd;
		if (currentPosition == 0) {
			outRect.set(0, 0, 0, 0);
			return;
		}

		if (lastPosition % 2 == 0) {
			isEnd = currentPosition == lastPosition || currentPosition == lastPosition - 1;
		} else {
			isEnd = currentPosition == lastPosition;
		}

		if (currentPosition % 2 == 1) {
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
