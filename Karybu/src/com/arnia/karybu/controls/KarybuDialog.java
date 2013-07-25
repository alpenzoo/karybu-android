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

public class KarybuDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;
	private ImageView imgIcon;
	private TextView txtTitle;
	private LinearLayout lytDialogContent;
	private TextView txtMessage;
	private Button btnPositive;
	private Button btnNegative;

	public KarybuDialog(Context context) {
		super(context);

		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.layout_custom_dialog);
		imgIcon = (ImageView) findViewById(R.id.img_diglog_icon);
		txtTitle = (TextView) findViewById(R.id.txt_dialog_title);
		lytDialogContent = (LinearLayout) findViewById(R.id.lyt_dialog_content);
		txtMessage = (TextView) findViewById(R.id.txt_dialog_message);
		btnPositive = (Button) findViewById(R.id.btn_dialog_ok);
		btnPositive.setOnClickListener(this);
		btnNegative = (Button) findViewById(R.id.btn_dialog_cancel);
		btnNegative.setOnClickListener(this);
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

	public void setView(View view) {
		lytDialogContent.addView(view);
		txtMessage.setVisibility(View.GONE);
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

	public void setPositiveButton(int resId) {
		setPositiveButton(context.getString(resId));
	}

	public void setPositiveButton(CharSequence text,
			android.view.View.OnClickListener onClickListener) {
		setPositiveButton(text);
		btnPositive.setOnClickListener(onClickListener);
	}

	public void setPositiveButton(int resId,
			android.view.View.OnClickListener onClickListener) {
		setPositiveButton(context.getString(resId), onClickListener);
	}

	public void setNegativeButton(CharSequence text) {
		btnNegative.setText(text);
		btnNegative.setVisibility(View.VISIBLE);
	}

	public void setNegativeButton(int resId) {
		setNegativeButton(context.getString(resId));
	}

	public void setNegativeButton(CharSequence text,
			android.view.View.OnClickListener onClickListener) {
		setNegativeButton(text);
		btnNegative.setOnClickListener(onClickListener);
	}

	public void setNegativeButton(int resId,
			android.view.View.OnClickListener onClickListener) {
		setNegativeButton(context.getString(resId), onClickListener);
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}

}
