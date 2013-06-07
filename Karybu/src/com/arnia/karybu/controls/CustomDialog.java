package com.arnia.karybu.controls;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.arnia.karybu.R;

public class CustomDialog extends Dialog {

	private ImageView imgIcon;
	private TextView txtTitle;
	private TextView txtMessage;
	private Button btnPositive;
	private Button btnNegative;

	public CustomDialog(Context context) {
		super(context);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.layout_custom_dialog);
		imgIcon = (ImageView) findViewById(R.id.img_diglog_icon);
		txtTitle = (TextView) findViewById(R.id.txt_dialog_title);
		txtMessage = (TextView) findViewById(R.id.txt_dialog_message);
		btnPositive = (Button) findViewById(R.id.btn_dialog_ok);
		btnNegative = (Button) findViewById(R.id.btn_dialog_cancel);
	}

	public void setIcon(int resId) {
		imgIcon.setImageResource(resId);
		imgIcon.setVisibility(View.VISIBLE);
	}

	public void setTitle(int resId) {
		txtTitle.setText(resId);
	}

	public void setTitle(CharSequence text) {
		txtTitle.setText(text);
	}

	public void setMessage(int resId) {
		txtMessage.setText(resId);
	}

	public void setMessage(CharSequence text) {
		txtMessage.setText(text);
	}

	public void setPositiveButton(CharSequence text) {
		btnPositive.setText(text);
		LinearLayout lytButtons = (LinearLayout) findViewById(R.id.lyt_dialog_buttons);
		lytButtons.setVisibility(View.VISIBLE);
	}

	public void setPositiveButton(CharSequence text,
			android.view.View.OnClickListener onClickListener) {
		setPositiveButton(text);
		btnPositive.setOnClickListener(onClickListener);
	}

	public void setNegativeButton(CharSequence text) {
		btnNegative.setText(text);
		btnNegative.setVisibility(View.VISIBLE);
	}

	public void setNegativeButton(CharSequence text,
			android.view.View.OnClickListener onClickListener) {
		setNegativeButton(text);
		btnNegative.setOnClickListener(onClickListener);
	}

}
