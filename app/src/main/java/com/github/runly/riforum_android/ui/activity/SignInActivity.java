package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.utils.RegisterCheck;
import com.github.runly.riforum_android.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-8.
 */

public class SignInActivity extends TopBarActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }

    private void init() {
        EditText accountEditText = (EditText) findViewById(R.id.account);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        EditText nicknameEditText = (EditText) findViewById(R.id.nickname);

        findViewById(R.id.sign_button).setOnClickListener(v -> {
            String account = accountEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String nickname = nicknameEditText.getText().toString();

            Map<String, String> map = new HashMap<>();
            //判断是否为邮箱
            if (RegisterCheck.isEmail(account)) {
                map.put("email", account);
            } else if (RegisterCheck.isPhone(account)) {
                map.put("phone", account);
            } else {
                ToastUtil.makeShortToast(this, getString(R.string.sign_in_error_account));
                return;
            }
            if (!TextUtils.isEmpty(nickname)) {
                map.put("name", nickname);
            } else {
                ToastUtil.makeShortToast(this, getString(R.string.sign_in_nickname_empty));
                return;
            }
            if (!TextUtils.isEmpty(password) && password.length() >= 6) {
                map.put("password", password);
            } else {
                ToastUtil.makeShortToast(this, getString(R.string.sign_in_nickname_empty));
                return;
            }
            RetrofitFactory.getInstance().getUserService().siginIn(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(theResponse -> {
                        if (theResponse.code.equals("0")) {
                            ToastUtil.makeShortToast(this, theResponse.message);
                        } else {
                            ToastUtil.makeShortToast(this, getString(R.string.sign_in_successfully));
                        }
                    },throwable -> {

                    });

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        topBar.getTxtLeft().setText(getString(R.string.sign_in_txt_left));
    }
}
