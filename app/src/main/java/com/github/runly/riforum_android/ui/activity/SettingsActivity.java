package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.letteravatar.LetterAvatar;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.view.DeleteDialog;
import com.github.runly.riforum_android.utils.DataCleanManager;
import com.github.runly.riforum_android.utils.SdCardUtil;
import com.github.runly.riforum_android.utils.SharedPreferencesUtil;
import com.github.runly.riforum_android.utils.ToastUtil;
import com.github.runly.riforum_android.utils.UnitConvert;
import com.github.runly.riforum_android.utils.VersionUtil;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
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
		TextView nameTxt = (TextView) findViewById(R.id.setting_name);
		Button logoutButton = (Button) findViewById(R.id.logout_button);

		if (user != null) {
			CircleImageView userAvatar = (CircleImageView) findViewById(R.id.setting_avatar);
			if (!TextUtils.isEmpty(user.avatar)) {
				String avatarUrl = user.avatar + "?imageView2/1/w/" +
					UnitConvert.dp2Px(this, Constants.NORMAL_AVATAR_SIZE) + "/h/" +
					UnitConvert.dp2Px(this, Constants.NORMAL_AVATAR_SIZE) + "/format/webp";
				Glide.with(this)
					.load(avatarUrl)
					.crossFade()
					.into(userAvatar);
			} else {
				LetterAvatar.with(this)
					.canvasSizeDIP(Constants.NORMAL_AVATAR_SIZE, Constants.NORMAL_AVATAR_SIZE)
					.letterSizeDIP(Constants.NORMAL_AVATAR_SIZE / 2)
					.chineseFirstLetter(user.name, true)
					.letterColorResId(R.color.comment_bar_dictionary)
					.backgroundColorResId(R.color.item_dividing)
					.into(userAvatar);
			}

			nameTxt.setText(user.name);

			logoutButton.setEnabled(true);
			logoutButton.setOnClickListener(v -> {
				Map<String, Object> map = new HashMap<>();
				map.put("uid", user.id);
				map.put("token", user.token);

				RetrofitFactory.getInstance()
					.getUserService()
					.logout(map)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(response -> {
						if ("1".equals(response.code)) {
							SdCardUtil.removeUserFromSdCard(this);
							App.getInstance().setUser(null);
							SharedPreferencesUtil.saveValue(Constants.USER_ACCOUNT, "");
							SharedPreferencesUtil.saveValue(Constants.USER_PASSWORD, "");
							ToastUtil.makeShortToast(this, response.message);
							finish();
						}
					}, throwable -> {
						throwable.printStackTrace();
						ToastUtil.makeShortToast(this, getString(R.string.logout_failed));
					});
			});
		} else {
			nameTxt.setText(R.string.not_login);
			logoutButton.setEnabled(false);
			logoutButton.setTextColor(ContextCompat.getColor(this, R.color.color_737373));
		}

		TextView versionTxt = (TextView) findViewById(R.id.setting_version_txt);
		versionTxt.setText("V " + VersionUtil.getVersion(this));

		RelativeLayout settingVersionLayout = (RelativeLayout) findViewById(R.id.setting_version);
		settingVersionLayout.setOnClickListener(v ->
			new Handler().postDelayed(() ->
				ToastUtil.makeShortToast(this, getString(R.string.latest_version)), 700)
		);


		RelativeLayout modifyPasswordLayout = (RelativeLayout) findViewById(R.id.setting_password_modify);
		modifyPasswordLayout.setOnClickListener(v -> {
			if (App.getInstance().isLogin()) {
				Intent intent = new Intent(SettingsActivity.this, ModifyPasswordActivity.class);
				intent.putExtra(Constants.INTENT_USER_DATA, user);
				startActivity(intent);
			} else {
				ToastUtil.makeShortToast(this, getString(R.string.not_login));
			}
		});

		TextView cacheSize = (TextView) findViewById(R.id.cache_size);
		cacheSize.setText(DataCleanManager.getAllFolderSize(this));

		RelativeLayout cleanCache = (RelativeLayout) findViewById(R.id.setting_clear_cache);
		cleanCache.setOnClickListener(v -> {
			DeleteDialog dialog = new DeleteDialog(this);
			dialog.show();
			dialog.setTitle(getString(R.string.cache_clear));
			dialog.setContent(getString(R.string.cache_clear_detail));
			dialog.setPositiveListener(v1 -> {
				// 在io线程中删文件
				Observable.just(this)
					.map(settingsActivity -> {
						DataCleanManager.cleanApplicationData(settingsActivity);
						return true;
					})
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(b -> {
						cacheSize.setText(DataCleanManager.getAllFolderSize(this));
						dialog.cancel();
					});
			});
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		topBar.getTxtLeft().setText(R.string.setting);
	}
}
