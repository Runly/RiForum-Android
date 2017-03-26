package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.utils.GoToActivity;
import com.github.runly.riforum_android.utils.SdCardUtil;
import com.github.runly.riforum_android.utils.SharedPreferencesUtil;
import com.github.runly.riforum_android.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.github.runly.riforum_android.R.mipmap.user;

/**
 * Created by ranly on 17-3-26.
 */

public class ModifyPasswordActivity extends TopBarActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);

		EditText oldPasswordEdit = (EditText) findViewById(R.id.old_password_edit);
		EditText newPasswordEdit = (EditText) findViewById(R.id.new_password_edit);
		EditText surePasswordEdit = (EditText) findViewById(R.id.sure_new_password_edit);
		Button modifyButton = (Button) findViewById(R.id.modify_bt);
		modifyButton.setOnClickListener(v -> {
			String oldPassword = oldPasswordEdit.getText().toString();
			String newPassword = newPasswordEdit.getText().toString();
			String surePassword = surePasswordEdit.getText().toString();

			if (newPassword.length() < 6) {
				ToastUtil.makeShortToast(this, getString(R.string.password_error));
				return;
			}

			if (!newPassword.equals(surePassword)) {
				ToastUtil.makeShortToast(this, getString(R.string.password_different));
				return;
			}

			User user = (User) getIntent().getSerializableExtra(Constants.INTENT_USER_DATA);
			if (user == null) {
				return;
			}

			Map<String, Object> map = new HashMap<>();
			map.put("uid", user.id);
			map.put("old_password", oldPassword);
			map.put("new_password", newPassword);
			RetrofitFactory.getInstance()
				.getUserService()
				.modify_password(map)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(response -> {
					if ("1".equals(response.code)) {
						logout(user);
						ToastUtil.makeShortToast(this,
							getString(R.string.modify_password_successfully));
					} else {
						ToastUtil.makeShortToast(this, response.message);
					}
				}, Throwable::printStackTrace);
		});
	}

	private void logout(User user) {
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
					GoToActivity.goTo(this, LoginActivity.class);
					finish();
				}
			}, Throwable::printStackTrace);
	}

	@Override
	protected void onStart() {
		super.onStart();
		topBar.getTxtCenter().setText(R.string.password_modify);
	}

}
