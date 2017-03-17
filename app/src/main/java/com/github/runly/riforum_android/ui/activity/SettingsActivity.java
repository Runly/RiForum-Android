package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.utils.SdCardUtil;
import com.github.runly.riforum_android.utils.SharedPreferencesUtil;
import com.github.runly.riforum_android.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-3-17.
 */

public class SettingsActivity extends TopBarActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		init();
	}

	private void init() {
		User user = (User) getIntent().getSerializableExtra(Constants.INTENT_USER_DATA);
		Button logoutButton = (Button) findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(v -> {
			if (user == null)
				return;

			Map<String, Object> map = new HashMap<>();
			map.put("uid", user.id);
			map.put("token", user.token);

			SdCardUtil.removeUserFromSdCard(this);
			App.getInstance().setUser(null);
			SharedPreferencesUtil.saveValue(Constants.USER_ACCOUNT, "");
			SharedPreferencesUtil.saveValue(Constants.USER_PASSWORD, "");

			RetrofitFactory.getInstance()
				.getUserService()
				.logout(map)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(response -> {
					if ("1".equals(response.code)) {
						ToastUtil.makeLongToast(this, response.message);
					}
				}, Throwable::printStackTrace);
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		topBar.getTxtCenter().setText(R.string.setting);
	}
}
