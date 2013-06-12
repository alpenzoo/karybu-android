package com.arnia.karybu.controls;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.BufferType;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.R;

public class KarybuTextEditor extends KarybuFragment implements
		OnClickListener, TextWatcher {

	// Editor control
	private EditText txtContent;
	private CheckBox btnBold;
	private CheckBox btnItalic;
	private CheckBox btnUnderline;
	private ImageButton btnLink;
	private Button btnAddImage;

	private int styleStart = -1;
	private int cursorLoc = 0;

	private int tmpSelectionStart;
	private int tmpSelectionEnd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_text_editor, container,
				false);

		// Editor control
		txtContent = (EditText) view.findViewById(R.id.EDITOR_CONTENT);
		txtContent.addTextChangedListener(this);

		btnBold = (CheckBox) view.findViewById(R.id.EDITOR_BOLD);
		btnBold.setOnClickListener(this);
		btnItalic = (CheckBox) view.findViewById(R.id.EDITOR_ITALIC);
		btnItalic.setOnClickListener(this);
		btnUnderline = (CheckBox) view.findViewById(R.id.EDITOR_UNDERLINE);
		btnUnderline.setOnClickListener(this);
		btnLink = (ImageButton) view.findViewById(R.id.EDITOR_LINK);
		btnLink.setOnClickListener(this);
		btnAddImage = (Button) view.findViewById(R.id.EDITOR_ADD_IMAGE);
		btnAddImage.setOnClickListener(this);

		return view;
	}

	// Refresh current cursor position variable value
	private void refreshSelectionPosition() {
		if (txtContent.getSelectionStart() > txtContent.getSelectionEnd()) {
			tmpSelectionStart = txtContent.getSelectionEnd();
			tmpSelectionEnd = txtContent.getSelectionStart();
		} else {
			tmpSelectionStart = txtContent.getSelectionStart();
			tmpSelectionEnd = txtContent.getSelectionEnd();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		int position = Selection.getSelectionStart(txtContent.getText());

		if (position < 0) {
			position = 0;
		}

		if (position > 0) {

			if (styleStart > position || position > (cursorLoc + 1)) {
				// user changed cursor location, reset
				if (position - cursorLoc > 1) {
					// user pasted text
					styleStart = cursorLoc;
				} else {
					styleStart = position - 1;
				}
			}

			if (btnBold.isChecked()) {
				StyleSpan[] ss = s.getSpans(styleStart, position,
						StyleSpan.class);

				for (int i = 0; i < ss.length; i++) {
					if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
						s.removeSpan(ss[i]);
					}
				}
				s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
						styleStart, position,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (btnItalic.isChecked()) {
				StyleSpan[] ss = s.getSpans(styleStart, position,
						StyleSpan.class);

				for (int i = 0; i < ss.length; i++) {
					if (ss[i].getStyle() == android.graphics.Typeface.ITALIC) {
						s.removeSpan(ss[i]);
					}
				}
				s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
						styleStart, position,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (btnUnderline.isChecked()) {
				UnderlineSpan[] ss = s.getSpans(styleStart, position,
						UnderlineSpan.class);

				for (int i = 0; i < ss.length; i++) {
					s.removeSpan(ss[i]);
				}
				s.setSpan(new UnderlineSpan(), styleStart, position,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		cursorLoc = Selection.getSelectionStart(txtContent.getText());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.EDITOR_BOLD:
			boldClick();
			break;

		case R.id.EDITOR_ITALIC:
			italicClick();
			break;
		case R.id.EDITOR_UNDERLINE:
			underlineClick();
			break;
		case R.id.EDITOR_LINK:
			linkClick();
			break;
		}
	}

	private void boldClick() {
		int selectionStart = txtContent.getSelectionStart();

		styleStart = selectionStart;

		int selectionEnd = txtContent.getSelectionEnd();

		if (selectionStart > selectionEnd) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		if (selectionEnd > selectionStart) {
			Spannable str = txtContent.getText();
			StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd,
					StyleSpan.class);

			boolean exists = false;
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
					str.removeSpan(ss[i]);
					exists = true;
				}
			}

			if (!exists) {
				str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
						selectionStart, selectionEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			btnBold.setChecked(false);
		}
	}

	private void italicClick() {
		int selectionStart = txtContent.getSelectionStart();

		styleStart = selectionStart;

		int selectionEnd = txtContent.getSelectionEnd();

		if (selectionStart > selectionEnd) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		if (selectionEnd > selectionStart) {
			Spannable str = txtContent.getText();
			StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd,
					StyleSpan.class);

			boolean exists = false;
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].getStyle() == android.graphics.Typeface.ITALIC) {
					str.removeSpan(ss[i]);
					exists = true;
				}
			}

			if (!exists) {
				str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
						selectionStart, selectionEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			btnItalic.setChecked(false);
		}
	}

	private void underlineClick() {
		int selectionStart = txtContent.getSelectionStart();

		styleStart = selectionStart;

		int selectionEnd = txtContent.getSelectionEnd();

		if (selectionStart > selectionEnd) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		if (selectionEnd > selectionStart) {
			Spannable str = txtContent.getText();
			UnderlineSpan[] ss = str.getSpans(selectionStart, selectionEnd,
					UnderlineSpan.class);

			boolean exists = false;
			for (int i = 0; i < ss.length; i++) {
				str.removeSpan(ss[i]);
				exists = true;
			}

			if (!exists) {
				str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			btnUnderline.setChecked(false);
		}
	}

	private void linkClick() {
		refreshSelectionPosition();

		final KarybuDialog dialog = new KarybuDialog(activity);
		dialog.setTitle(R.string.link);

		final LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10, 0, 10, 0);

		final EditText txtLinkText = new EditText(getActivity());
		txtLinkText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtLinkText.setBackgroundResource(R.drawable.bg_edittext);
		txtLinkText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
		txtLinkText.setHint(getString(R.string.link_text));

		final EditText txtLinkUrl = new EditText(getActivity());
		txtLinkUrl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtLinkUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
		txtLinkUrl.setBackgroundResource(R.drawable.bg_edittext);
		txtLinkUrl.setHint(getString(R.string.link_url));

		if (tmpSelectionStart < tmpSelectionEnd) {
			txtLinkText.setText(txtContent.getText().subSequence(
					tmpSelectionStart, tmpSelectionEnd));
			txtLinkUrl.requestFocus();
		}

		layout.addView(txtLinkText);
		layout.addView(txtLinkUrl);

		dialog.setView(layout);
		dialog.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
				txtContent.getText().replace(tmpSelectionStart,
						tmpSelectionEnd, txtLinkText.getText());
				tmpSelectionEnd = tmpSelectionStart
						+ txtLinkText.getText().length();
				String linkUrl = txtLinkUrl.getText().toString();
				Spannable str = txtContent.getText();
				str.setSpan(new URLSpan(linkUrl), tmpSelectionStart,
						tmpSelectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton(R.string.cancel);
		dialog.show();
	}

	public String getContent() {
		return Html.toHtml(txtContent.getText());
	}

	public void setContent(String htmlContent) {
		txtContent.setText(Html.fromHtml(htmlContent), BufferType.SPANNABLE);
	}

	public void requestFocus() {
		txtContent.requestFocus();
	}

}
