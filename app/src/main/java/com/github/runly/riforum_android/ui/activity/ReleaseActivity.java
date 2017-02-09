package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.runly.riforum_android.R;

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
        topBar.getTxtCenter().setText(getString(R.string.release_txt_center));
        topBar.getTxtRight().setText(getString(R.string.release_txt_right));
    }
}
