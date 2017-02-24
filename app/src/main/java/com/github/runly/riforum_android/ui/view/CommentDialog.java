package com.github.runly.riforum_android.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.runly.riforum_android.R;

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
        mButton = (Button) layout.findViewById(R.id.reply_button);
    }

    public void setReplayButtonOnClickListener(View.OnClickListener listenner) {
        mButton.setOnClickListener(listenner);
    }
}
