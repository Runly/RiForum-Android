package com.github.runly.riforum_android.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.utils.UnitConvert;

/**
 * Created by ranly on 17-2-24.
 */

public class CommentDialog extends Dialog {
    private Context mContext;
    private Button mButton;

    public CommentDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommentDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_comment_dialog, null);
        this.setContentView(layout);
        Window dialog_window = this.getWindow();
        if (dialog_window != null) {
            //获取到LayoutParams
            WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
            if (dialog_window_attributes != null) {
                //设置宽度
                dialog_window_attributes.width= UnitConvert.dipToPixels(mContext, 360);
                dialog_window.setAttributes(dialog_window_attributes);
            }
        }
        mButton = (Button) layout.findViewById(R.id.reply_button);
    }

    public void setReplayButtonOnClickListener(View.OnClickListener listenner) {
        mButton.setOnClickListener(listenner);
    }
}
