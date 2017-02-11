package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.runly.riforum_android.R;

/**
 * Created by ranly on 17-2-8.
 */

public class SignInActivity extends TopBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.to_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        topBar.getTxtLeft().setText(getString(R.string.sign_in_txt_left));
    }
}
