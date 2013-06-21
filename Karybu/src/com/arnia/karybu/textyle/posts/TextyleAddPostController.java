package com.arnia.karybu.textyle.posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuResponse;
import com.arnia.karybu.controls.KarybuTextEditor;

public class TextyleAddPostController extends KarybuFragment implements
		OnClickListener {

	private EditText etxtTitle;
	private EditText etxtUrl;
	private KarybuTextEditor editor;
	private Button btnSave;
	private Button btnSaveAndPublish;
	private MainActivityController mainActivity;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mainActivity = (MainActivityController) activity;

		view = inflater.inflate(R.layout.layout_textyle_new_post, container,
				false);

		etxtTitle = (EditText) view.findViewById(R.id.ADD_POST_POST_TITLE);
		etxtUrl = (EditText) view.findViewById(R.id.ADD_POST_POST_URL);

		editor = new KarybuTextEditor();
		addNestedFragment(R.id.HTML_EDITOR, editor, "add_post_html_editor");

		btnSave = (Button) view.findViewById(R.id.ADD_POST_SAVE_BUTTON);
		btnSave.setOnClickListener(this);
		btnSaveAndPublish = (Button) view
				.findViewById(R.id.ADD_POST_SAVE_AND_PUBLISH_BUTTON);
		btnSaveAndPublish.setOnClickListener(this);

		return view;
	}

	// asynctask for saving the post
	private class SavePostAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(getActivity(), "Saving...");
		}

		@Override
		protected Object doInBackground(Object... params) {

			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
					+ "<vid><![CDATA["
					+ mainActivity.getSelectedTextyle().domain
					+ "]]></vid>\n"
					+ "<publish><![CDATA[N]]></publish>\n"
					+ "<_filter><![CDATA[save_post]]></_filter>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<title><![CDATA["
					+ etxtTitle.getText().toString()
					+ "]]></title>\n"
					+ getAliasXmlString()
					+ "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
					+ "<content><![CDATA[<p>"
					+ editor.getContent()
					+ "</p>]]></content>\n"
					+ "<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it?"
					+ " The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n"
					+ "<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";

			KarybuHost.getINSTANCE().postRequest("/index.php", xmlForSaving);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			dismissProgress();
			Toast.makeText(getActivity(), "Save post success.",
					Toast.LENGTH_LONG).show();
		}

	}

	// async task for publishing the post

	// for publish an post, firstly, it must be saved and then published
	private class PublishPostAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(getActivity(), "Publishing...");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// firstly, the post is saved
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
					+ "<vid><![CDATA["
					+ mainActivity.getSelectedTextyle().domain
					+ "]]></vid>\n"
					+ "<publish><![CDATA[N]]></publish>\n"
					+ "<_filter><![CDATA[save_post]]></_filter>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<title><![CDATA["
					+ etxtTitle.getText().toString()
					+ "]]></title>\n"
					+ getAliasXmlString()
					+ "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
					+ "<content><![CDATA[<p>"
					+ editor.getContent()
					+ "</p>]]></content>\n"
					+ "<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it?"
					+ " The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n"
					+ "<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";

			String responseAtRequest = KarybuHost.getINSTANCE().postRequest(
					"/index.php", xmlForSaving);

			Serializer serializer = new Persister();
			Reader reader = new StringReader(responseAtRequest);

			// the response has the document_srl, which is used for publishing
			KarybuResponse response = null;
			try {
				response = serializer.read(KarybuResponse.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// the xml for publishing
			String xmlForPublishing = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n"
					+ "<params>\n"
					+ "<_filter><![CDATA[publish_post]]></_filter>\n"
					+ "<act><![CDATA[procTextylePostPublish]]></act>\n"
					+ "<document_srl><![CDATA["
					+ response.document_srl
					+ "]]></document_srl>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<vid><![CDATA["
					+ mainActivity.getSelectedTextyle().domain
					+ "]]></vid>\n"
					+ "<trackback_charset><![CDATA[UTF-8]]></trackback_charset>\n"
					+ "<use_alias><![CDATA[N]]></use_alias>\n"
					+ "<allow_comment><![CDATA[Y]]></allow_comment>\n"
					+ "<allow_trackback><![CDATA[Y]]></allow_trackback>\n"
					+ "<subscription><![CDATA[N]]></subscription>\n"
					+ "<module><![CDATA[textyle]]></module>\n"
					+ "</params>\n</methodCall>";

			KarybuHost.getINSTANCE()
					.postRequest("/index.php", xmlForPublishing);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();
			Toast.makeText(getActivity(), "Publish post success.",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ADD_POST_SAVE_BUTTON:
			if (validateInput()) {
				if (mainActivity.getSelectedTextyle() == null) {
					Toast.makeText(
							activity,
							getString(R.string.msg_there_is_no_textyle_in_this_site),
							Toast.LENGTH_LONG).show();
				} else {
					SavePostAsyncTask saveTask = new SavePostAsyncTask();
					saveTask.execute();
				}
			}
			break;
		case R.id.ADD_POST_SAVE_AND_PUBLISH_BUTTON:
			if (validateInput()) {
				if (mainActivity.getSelectedTextyle() == null) {
					Toast.makeText(
							activity,
							getString(R.string.msg_there_is_no_textyle_in_this_site),
							Toast.LENGTH_LONG).show();
				} else {
					PublishPostAsyncTask publishTask = new PublishPostAsyncTask();
					publishTask.execute();
				}
			}
			break;
		}
	}

	private boolean validateInput() {
		if (etxtTitle.getText().toString().trim().length() == 0) {
			Toast.makeText(getActivity(), "Title is required.",
					Toast.LENGTH_LONG).show();
			etxtTitle.requestFocus();
			return false;
		}
		if (editor.getContent().trim().length() == 0) {
			Toast.makeText(getActivity(), "Body is required.",
					Toast.LENGTH_LONG).show();
			editor.requestFocus();
			return false;
		}
		return true;
	}

	private String getAliasXmlString() {
		String url = etxtUrl.getText().toString().trim();
		if (url.length() == 0)
			return url;

		// if (url.startsWith(mainSite))
		// url = url.replace(mainSite, "");

		if (url.startsWith("http") || url.startsWith("www")) {
			Pattern pattern = Pattern.compile("[a-zA-Z]/");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				int inx = matcher.start();
				url = url.substring(inx + 1);
			} else
				url = "";
		}

		if (url.startsWith("/"))
			url = url.substring(1);

		if (url.length() > 0)
			url = "<alias><![CDATA[" + url + "]]></alias>\n";

		return url;
	}
}
