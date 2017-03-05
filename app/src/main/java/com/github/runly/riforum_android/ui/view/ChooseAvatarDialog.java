package com.github.runly.riforum_android.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;

/**
 * Created by ranly on 17-3-3.
 */

public class ChooseAvatarDialog extends Dialog {
    private String avatarUrl;
    private Context mContext;
    private Button button;

    public ChooseAvatarDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public ChooseAvatarDialog(Context context, String avatarUrl) {
        super(context);
        this.avatarUrl = avatarUrl;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_choose_avatar_dialog);

        button = (Button) findViewById(R.id.button);

        ImageView avatar = (ImageView) findViewById(R.id.dialog_avatar);
        if (!TextUtils.isEmpty(avatarUrl)){
            Glide.with(mContext)
                    .load(avatarUrl)
                    .crossFade()
                    .into(avatar);
        }
    }

    public void setButtonListener(View.OnClickListener listener) {
        button.setOnClickListener(listener);
    }
}
