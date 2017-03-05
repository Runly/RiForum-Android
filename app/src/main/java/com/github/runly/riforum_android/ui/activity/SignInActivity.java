package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.utils.RegisterCheck;
import com.github.runly.riforum_android.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-8.
 */

public class SignInActivity extends TopBarActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }

    private void init() {
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.to_sign_in).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        topBar.getTxtLeft().setText(getString(R.string.sign_in_txt_left));
    }

    private void signIn() {
        EditText accountEditText = (EditText) findViewById(R.id.account);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        EditText nicknameEditText = (EditText) findViewById(R.id.nickname);
        String account = accountEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String nickname = nicknameEditText.getText().toString();

        Map<String, Object> map = new HashMap<>();
        // 邮箱, 手机号验证
        if (RegisterCheck.isEmail(account)) {
            map.put("email", account);
        } else if (RegisterCheck.isPhone(account)) {
            map.put("phone", account);
        } else {
            ToastUtil.makeShortToast(this, getString(R.string.error_account));
            return;
        }

        // 昵称, 密码非空
        if (!TextUtils.isEmpty(nickname)) {
            map.put("name", nickname);
        } else {
            ToastUtil.makeShortToast(this, getString(R.string.sign_in_nickname_empty));
            return;
        }
        if (!TextUtils.isEmpty(password) && password.length() >= 6) {
            map.put("password", password);
        } else {
            ToastUtil.makeShortToast(this, getString(R.string.password_error));
            return;
        }

        RetrofitFactory.getInstance().getUserService().signIn(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(theResponse -> {
                            if (theResponse.code.equals("0")) {
                                ToastUtil.makeShortToast(this, theResponse.message);
                            } else {
                                ToastUtil.makeShortToast(this, getString(R.string.sign_in_successfully));
                                finish();
                            }
                        },
                        throwable -> ToastUtil.makeShortToast(this, getString(R.string.sign_in_failed))
                );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                signIn();
                break;

            case R.id.to_sign_in:
                finish();
                break;

            default:
                break;
        }
    }
}
