package com.github.runly.riforum_android.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;

/**
 * Created by ranly on 17-3-24.
 */

public class DeleteDialog extends Dialog {
	private Button sure;
	private TextView title;
	private TextView content;

	public DeleteDialog(@NonNull Context context) {
		super(context);
	}

	public DeleteDialog(@NonNull Context context, @StyleRes int themeResId) {
		super(context, themeResId);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_delete_dialog);
		Window dialog_window = this.getWindow();
		if (dialog_window != null) {
			//获取到LayoutParams
			WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
			if (dialog_window_attributes != null) {
				//设置宽度
				dialog_window_attributes.width = (int) (Constants.SCREEN_WIDTH * 0.9);
				dialog_window.setAttributes(dialog_window_attributes);
			}
		}

		sure = (Button) findViewById(R.id.sure);
		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(v -> this.cancel());
		title = (TextView) findViewById(R.id.title);
		content = (TextView) findViewById(R.id.content);
	}

	public void setPositiveListener(View.OnClickListener positiveListener) {
		sure.setOnClickListener(positiveListener);
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void setContent(String content) {
		this.content.setText(content);
	}
}
