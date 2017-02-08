package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.utils.Constant;

/**
 * Created by ranly on 17-2-8.
 */

public class SignInActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign_in);
        TopBar topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setPadding(0, Constant.STATUS_HEIGHT, 0, 0);
        topBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBase));
        topBar.getImgLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.to_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
