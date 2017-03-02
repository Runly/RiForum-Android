package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.view.TopBar;

/**
 * Created by ranly on 17-3-1.
 */

public class UserInfoActivity extends TopBarActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }
}
