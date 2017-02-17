package com.github.runly.riforum_android.ui.activity;

import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.view.CircularImageView;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.utils.Constant;
import com.github.runly.riforum_android.utils.UnitConvert;


/**
 * Created by ranly on 17-2-9.
 */

public class TopBarActivity extends BaseActivity {
    protected TopBar topBar;

    @Override
    protected void onResume() {
        super.onResume();
        addTopBar();
    }

    protected void addTopBar() {
        topBar = new TopBar(this, null);
        topBar.setPadding(0, Constant.STATUS_HEIGHT, 0, 0);
        topBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBase));
        topBar.getTxtLeft().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        CircularImageView cImg = (CircularImageView) topBar.getImgLeft();
        ViewGroup.LayoutParams lp = cImg.getLayoutParams();
        lp.height = UnitConvert.dipToPixels(this, 24);
        lp.width = UnitConvert.dipToPixels(this, 24);
        cImg.setLayoutParams(lp);
        topBar.getImgLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(topBar, layoutParams);
    }
}
