package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.github.runly.riforum_android.R;

/**
 * Created by ranly on 17-2-8.
 */

public class LoginActivity extends TopBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.to_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("topbar height", topBar.getHeight()+"");
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        topBar.getTxtLeft().setText(getString(R.string.login_txt_left));
    }
}
