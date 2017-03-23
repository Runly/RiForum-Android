package com.github.runly.riforum_android.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.interfaces.OnChooseGender;

/**
 * Created by ranly on 17-3-2.
 */

public class GenderDialog extends Dialog {
    private OnChooseGender onChooseGender;
    private int currentGender;

    public GenderDialog(Context context, OnChooseGender onChooseGender, int currentGender) {
        super(context);
        this.onChooseGender = onChooseGender;
        this.currentGender = currentGender;
    }

    public GenderDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_denger_dialog);

        Window dialog_window = this.getWindow();
        if (dialog_window != null) {
            //获取到LayoutParams
            WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
            if (dialog_window_attributes != null) {
                //设置宽度
                dialog_window_attributes.width= (int) (Constants.SCREEN_WIDTH * 0.9);
                dialog_window.setAttributes(dialog_window_attributes);
            }
        }

        findViewById(R.id.sure).setOnClickListener(v->cancel());

        CheckBox cbxMale = (CheckBox) findViewById(R.id.cbx_male);
        CheckBox cbxFemale = (CheckBox) findViewById(R.id.cbx_female);

        if (currentGender == 0)
            cbxMale.setChecked(true);
        else
            cbxFemale.setChecked(true);

        cbxMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                onChooseGender.chooseGender(Constants.MALE);
                cbxFemale.setChecked(false);
            }else {
                if (!cbxFemale.isChecked() && !cbxMale.isChecked())
                    onChooseGender.chooseGender(Constants.NO_GENDER);
            }
        });

        cbxFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                onChooseGender.chooseGender(Constants.FEMALE);
                cbxMale.setChecked(false);
            } else {
                if (!cbxFemale.isChecked() && !cbxMale.isChecked())
                    onChooseGender.chooseGender(Constants.NO_GENDER);
            }
        });
    }
}
