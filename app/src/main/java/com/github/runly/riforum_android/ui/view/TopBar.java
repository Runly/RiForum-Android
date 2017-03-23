package com.github.runly.riforum_android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.utils.UnitConvert;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ranly on 17-2-6.
 */

public class TopBar extends LinearLayout {
	private Context mContext;
	private CircleImageView imgLeft;
	private TextView txtLeft, txtCenter, txtRight;
	private ImageView imgRight;

	private int imgLeftWidth, imgLeftHeight, imgLeftDefault, imgRightWidth, imgRightHeight, imgRightDefault, txtLeftSize, txtLeftColor, txtCenterSize, txtCenterColor, txtRightSize, txtRightColor;
	private String txtLeftStr, txtCenterStr, txtRightStr;

	public TopBar(Context context) {
		super(context);
		mContext = context;
	}

	public TopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

		imgLeftWidth = UnitConvert.dp2Px(mContext, array.getInt(R.styleable.TopBar_img_left_width, 24));
		imgLeftHeight = UnitConvert.dp2Px(mContext, array.getInt(R.styleable.TopBar_img_left_height, 24));
		imgLeftDefault = array.getResourceId(R.styleable.TopBar_img_left_default, R.mipmap.back);

		imgRightWidth = UnitConvert.dp2Px(mContext, array.getInt(R.styleable.TopBar_img_right_width, 24));
		imgRightHeight = UnitConvert.dp2Px(mContext, array.getInt(R.styleable.TopBar_img_right_height, 24));
		imgRightDefault = array.getResourceId(R.styleable.TopBar_img_right_default, -1);

		txtLeftSize = array.getInt(R.styleable.TopBar_txt_left_size, 16);
		txtLeftStr = array.getString(R.styleable.TopBar_txt_left);
		txtLeftColor = array.getColor(R.styleable.TopBar_txt_left_color, Color.WHITE);

		txtCenterSize = array.getInt(R.styleable.TopBar_txt_center_size, 16);
		txtCenterStr = array.getString(R.styleable.TopBar_txt_center);
		txtCenterColor = array.getColor(R.styleable.TopBar_txt_center_color, Color.WHITE);

		txtRightSize = array.getInt(R.styleable.TopBar_txt_right_size, 16);
		txtRightStr = array.getString(R.styleable.TopBar_txt_right);
		txtRightColor = array.getColor(R.styleable.TopBar_txt_right_color, Color.WHITE);

		array.recycle();
		init();
	}

	public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	private void init() {
		View mTopBar = View.inflate(mContext, R.layout.view_top_bar, this);
		imgLeft = (CircleImageView) mTopBar.findViewById(R.id.img_left);
		ViewGroup.LayoutParams imgLeftLayoutParams = imgLeft.getLayoutParams();
		imgLeftLayoutParams.width = imgLeftWidth;
		imgLeftLayoutParams.height = imgLeftHeight;
		imgLeft.setLayoutParams(imgLeftLayoutParams);
		if (imgLeftDefault != -1)
			imgLeft.setImageResource(imgLeftDefault);

		imgRight = (ImageView) mTopBar.findViewById(R.id.img_right);
		ViewGroup.LayoutParams imgRightLayoutParams = imgRight.getLayoutParams();
		imgRightLayoutParams.width = imgRightWidth;
		imgRightLayoutParams.height = imgRightHeight;
		imgRight.setLayoutParams(imgRightLayoutParams);
		if (imgRightDefault != -1)
			imgRight.setImageResource(imgRightDefault);

		txtLeft = (TextView) mTopBar.findViewById(R.id.txt_left);
		txtLeft.setText(txtLeftStr);
		txtLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtLeftSize);
		txtLeft.setTextColor(txtLeftColor);

		txtCenter = (TextView) mTopBar.findViewById(R.id.txt_center);
		txtCenter.setText(txtCenterStr);
		txtCenter.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtCenterSize);
		txtCenter.setTextColor(txtCenterColor);

		txtRight = (TextView) mTopBar.findViewById(R.id.txt_right);
		txtRight.setText(txtRightStr);
		txtRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtRightSize);
		txtRight.setTextColor(txtRightColor);
	}

	public ImageView getImgLeft() {
		return imgLeft;
	}

	public TextView getTxtLeft() {
		return txtLeft;
	}

	public TextView getTxtCenter() {
		return txtCenter;
	}

	public TextView getTxtRight() {
		return txtRight;
	}

	public ImageView getImgRight() {
		return imgRight;
	}
}
