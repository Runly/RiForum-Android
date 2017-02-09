package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.utils.UnitConvert;

/**
 * Created by ranly on 17-2-8.
 */

public class ReleaseActivity extends TopBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 配置topBar
        topBar.getTxtCenter().setText(getString(R.string.release_txt_center));
        TextView txtRight = topBar.getTxtRight();
        txtRight.setGravity(Gravity.CENTER);
        txtRight.setText(getString(R.string.release_txt_right));
        ViewGroup.LayoutParams layoutParams = txtRight.getLayoutParams();
        layoutParams.width = UnitConvert.dipToPixels(this, 48);
        layoutParams.height = UnitConvert.dipToPixels(this, 24);
        txtRight.setLayoutParams(layoutParams);
        txtRight.setBackground(ContextCompat.getDrawable(this, R.drawable.release_text_border));
    }
}
