package com.arnia.karybu.textyle.comments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuTextyle;

public class TextyleCommentsReplyController extends KarybuFragment {
	private TextView replyTextView;
	private String document_srl;
	private String comment_srl;
	private KarybuTextyle textyle;
	private Button postItButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.layout_textyle_comment_reply, container, false);
		replyTextView = (TextView) view
				.findViewById(R.id.TEXTYLE_COMMENT_REPLYTEXTVIEW);
		postItButton = (Button) view
				.findViewById(R.id.TEXTYLE_COMMENT_POSTBUTTON);
		postItButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PostReplyAsyncTask task = new PostReplyAsyncTask();
				task.execute();
			}
		});
		Bundle args = getArguments();
		textyle = (KarybuTextyle) args.getSerializable("textyle");
		document_srl = args.getString("document_srl");
		comment_srl = args.getString("comment_srl");

		return view;
	}

	private class PostReplyAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String postCommentXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n"
					+ "<_filter><![CDATA[insert_comment]]></_filter>\n"
					+ "<act><![CDATA[procTextyleInsertComment]]></act>\n"
					+ "<vid><![CDATA["
					+ textyle.domain
					+ "]]></vid>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<document_srl><![CDATA["
					+ document_srl
					+ "]]></document_srl>\n"
					+ "<content><![CDATA[<p>"
					+ replyTextView.getText().toString()
					+ "</p>]]></content>\n"
					+ "<parent_srl><![CDATA["
					+ comment_srl
					+ "]]></parent_srl>\n"
					+ "<module><![CDATA[textyle]]></module>\n</params>\n"
					+ "</methodCall>";

			KarybuHost.getINSTANCE().postRequest("/index.php", postCommentXML);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// finish();
			((MainActivityController) activity).backwardScreen();
			super.onPostExecute(result);
		}

	}
}
