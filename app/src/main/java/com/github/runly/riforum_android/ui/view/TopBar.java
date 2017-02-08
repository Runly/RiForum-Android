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
import com.pkmmte.view.CircularImageView;

/**
 * Created by ranly on 17-2-6.
 */

public class TopBar extends LinearLayout {
    private Context mContext;
    private CircularImageView avatarImgV;
    private TextView titleTxtV;

    private int avatarWidth, avatarHeight, titleSize, titleColor;
    private String titleContent;

    public TopBar(Context context) {
        super(context);
        mContext = context;
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        avatarWidth = UnitConvert.dipToPixels(mContext, array.getInt(R.styleable.TopBar_avatar_width, 40));
        avatarHeight = UnitConvert.dipToPixels(mContext, array.getInt(R.styleable.TopBar_avatar_height, 40));
        titleSize = array.getInt(R.styleable.TopBar_title_size, 22);
        titleContent = array.getString(R.styleable.TopBar_title_content);
        titleColor = array.getColor(R.styleable.TopBar_title_color, Color.WHITE);
        array.recycle();

        init();
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private void init() {
        View mTopBar = View.inflate(mContext, R.layout.top_bar, this);
        avatarImgV = (CircularImageView) mTopBar.findViewById(R.id.avatar);
        ViewGroup.LayoutParams layoutParams = avatarImgV.getLayoutParams();
        layoutParams.width = avatarWidth;
        layoutParams.height = avatarHeight;
        avatarImgV.setLayoutParams(layoutParams);

        titleTxtV = (TextView) mTopBar.findViewById(R.id.title);
        titleTxtV.setText(titleContent);
        titleTxtV.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
        titleTxtV.setTextColor(titleColor);
    }

    public ImageView getAvatarImgV() {
        return avatarImgV;
    }

    public TextView getTitleTxtV() {
        return titleTxtV;
    }
}
