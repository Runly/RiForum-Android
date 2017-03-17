package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.runly.riforum_android.R;

/**
 * Created by ranly on 17-3-17.
 */

public class AboutActivity extends TopBarActivity{

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	@Override
	protected void onStart() {
		super.onStart();
		topBar.getTxtCenter().setText(R.string.about);
	}
}
