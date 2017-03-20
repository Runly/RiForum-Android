package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.utils.VersionUtil;

/**
 * Created by ranly on 17-3-17.
 */

public class AboutActivity extends TopBarActivity{

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		init();
	}

	private void init() {
		TextView versionTxt = (TextView) findViewById(R.id.about_version);
		versionTxt.setText("Ver " + VersionUtil.getVersion(this));
	}

	@Override
	protected void onStart() {
		super.onStart();
		topBar.getTxtLeft().setText(R.string.about);
	}
}
