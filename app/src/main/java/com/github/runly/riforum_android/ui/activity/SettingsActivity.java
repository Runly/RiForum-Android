package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.letteravatar.LetterAvatar;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.utils.SdCardUtil;
import com.github.runly.riforum_android.utils.SharedPreferencesUtil;
import com.github.runly.riforum_android.utils.ToastUtil;
import com.github.runly.riforum_android.utils.UnitConvert;
import com.github.runly.riforum_android.utils.VersionUtil;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
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
							ToastUtil.makeShortToast(this, response.message);
							finish();
						}
					}, Throwable::printStackTrace);
			});
		} else {
			nameTxt.setText(R.string.not_login);
			logoutButton.setEnabled(false);
			logoutButton.setTextColor(ContextCompat.getColor(this, R.color.color_737373));
		}

		TextView versionTxt = (TextView) findViewById(R.id.setting_version_txt);
		versionTxt.setText("V" + VersionUtil.getVersion(this));

	}

	@Override
	protected void onStart() {
		super.onStart();
		topBar.getTxtLeft().setText(R.string.setting);
	}
}
