package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.utils.GoToActivity;
import com.github.runly.riforum_android.utils.RegisterCheck;
import com.github.runly.riforum_android.utils.SdCardUtil;
import com.github.runly.riforum_android.utils.SharedPreferencesUtil;
import com.github.runly.riforum_android.utils.ToastUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-8.
 */

public class LoginActivity extends TopBarActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init()
    {
        findViewById(R.id.to_sign_in).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
    }

    private void login() {
        EditText accountEditText = (EditText) findViewById(R.id.account);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String account = accountEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Map<String, Object> map = new HashMap<>();
        //判断是否为邮箱
        if (RegisterCheck.isEmail(account)) {
            map.put("email", account);
        } else if (RegisterCheck.isPhone(account)) {
            map.put("phone", account);
        } else {
            ToastUtil.makeShortToast(this, getString(R.string.error_account));
            return;
        }

        if (!TextUtils.isEmpty(password) && password.length() >= 6) {
            map.put("password", password);
        } else {
            ToastUtil.makeShortToast(this, getString(R.string.password_error));
            return;
        }

        RetrofitFactory.getInstance().getUserService().login(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(theResponse -> {
                            if (theResponse.code.equals("0")) {
                                ToastUtil.makeShortToast(this, theResponse.message);
                            } else {
                                ToastUtil.makeShortToast(this, getString(R.string.login_successfully));
                                App.getInstance().setUser(theResponse.data);
                                SharedPreferencesUtil.saveValue(Constants.USER_ACCOUNT, account);
                                SharedPreferencesUtil.saveValue(Constants.USER_PASSWORD, password);
                                finish();
                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            ToastUtil.makeShortToast(this, getString(R.string.login_failed));
                        }
                );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_sign_in:
                GoToActivity.goTo(this, SignInActivity.class);
                break;

            case R.id.login_button:
                login();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        topBar.getTxtLeft().setText(getString(R.string.login_txt_left));
    }
}
