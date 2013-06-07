package com.arnia.karybu.textyle.posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.arnia.karybu.R;
import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.classes.KarybuDocument;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuTextyle;
import com.arnia.karybu.controls.KarybuTextEditor;

public class TextyleEditPostContentController extends KarybuFragment
		implements OnClickListener {
	private String title;
	private KarybuTextyle textyle;
	private String document_srl;
	private String category_srl;
	private String content;
	private Button deleteButton;

	private EditText titleEditText;
	private KarybuTextEditor htmlEditor;
	private EditText postUrl;
	private Button saveButton;
	private Button saveAndPublishButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		htmlEditor = new KarybuTextEditor();
		View view = inflater.inflate(R.layout.layout_textyle_edit_post,
				container, false);
		addNestedFragment(R.id.HTML_EDITOR, htmlEditor,
				"add_post_html_editor");

		titleEditText = (EditText) view
				.findViewById(R.id.ADD_POST_POST_TITLE);
		postUrl = (EditText) view.findViewById(R.id.ADD_POST_POST_URL);

		saveAndPublishButton = (Button) view
				.findViewById(R.id.ADD_POST_SAVE_AND_PUBLISH_BUTTON);
		saveButton = (Button) view
				.findViewById(R.id.ADD_POST_SAVE_BUTTON);

		saveAndPublishButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);

		deleteButton = (Button) view
				.findViewById(R.id.ADD_POST_DELETE_BUTTON);

		deleteButton.setOnClickListener(this);

		Bundle args = getArguments();
		title = args.getString("title");
		textyle = (KarybuTextyle) args.getSerializable("textyle");
		document_srl = args.getString("document_srl");
		category_srl = args.getString("category_srl");

		titleEditText.setText(title);

		KarybuFragment.startProgress(getActivity(), "Logging...");
		LoadContentAsyncTask task = new LoadContentAsyncTask();
		task.execute();

		return view;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.ADD_POST_SAVE_BUTTON) {
			KarybuFragment.startProgress(getActivity(), "Saving...");
			SavePostAsyncTask task = new SavePostAsyncTask();
			task.execute();
		} else if (v.getId() == R.id.ADD_POST_SAVE_AND_PUBLISH_BUTTON) {
			KarybuFragment.startProgress(getActivity(), "Save and publish...");
			PublishPostAsyncTask task = new PublishPostAsyncTask();
			task.execute();
		} else if (v.getId() == R.id.ADD_POST_DELETE_BUTTON) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage("Do you want to delete this post?")
					.setTitle("Attention")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									KarybuFragment.startProgress(getActivity(),
											"Deleting...");
									DeleteAsyncTask deleteTask = new DeleteAsyncTask();
									deleteTask.execute();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {

								}
							}).show();
		}
	}

	private String getAliasXmlString() {
		String url = postUrl.getText().toString().trim();
		if (url.length() == 0)
			return url;

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

	// async taks for loading the content of the post
	private class LoadContentAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			content = KarybuHost
					.getINSTANCE()
					.getRequest(
							"/index.php?XDEBUG_SESSION_START=netbeans-xdebug&module=mobile_communication&act=procmobile_communicationContentForPost&module_srl="
									+ textyle.module_srl
									+ "&document_srl="
									+ document_srl);
			Serializer serializer = new Persister();

			Reader reader = new StringReader(content);
			Log.i("KarybuDocument", content);
			try {
				return serializer.read(KarybuDocument.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			if (result != null) {

				KarybuDocument document = (KarybuDocument) result;
				htmlEditor.setContent(new String(Base64.decode(
						document.content, Base64.DEFAULT)));
				postUrl.setText(document.alias);
			}
			dismissProgress();
		}
		// }
	}

	// asynctask for saving the post
	private class SavePostAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall><params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
					+ "<vid><![CDATA["
					+ textyle.domain
					+ "]]></vid>\n"
					+ "<publish><![CDATA[N]]></publish>\n"
					+ "<_filter><![CDATA[save_post]]></_filter>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<title><![CDATA["
					+ titleEditText.getText().toString()
					+ "]]></title>\n"
					+ getAliasXmlString()
					+ "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
					+ "<content><![CDATA[<p>"
					+ htmlEditor.getContent()
					+ "</p>]]></content>\n"
					+ "<document_srl><![CDATA["
					+ document_srl
					+ "]]></document_srl>\n"
					+ "<editor_sequence><![CDATA["
					+ document_srl
					+ "]]></editor_sequence>\n"
					+ "<module><![CDATA[textyle]]></module>\n"
					+ "</params>\n</methodCall>\n";

			KarybuHost.getINSTANCE().postRequest("/index.php", xmlForSaving);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			dismissProgress();
			// finish();
		}

	}

	// asynctask that deletes the post
	private class DeleteAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String xmlForDelete = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n"
					+ "<document_srl><![CDATA["
					+ document_srl
					+ "]]></document_srl>\n"
					+ "<page><![CDATA[1]]></page>\n"
					+ "<module><![CDATA[textyle]]></module>\n"
					+ "<act><![CDATA[procTextylePostTrash]]></act>\n"
					+ "<vid><![CDATA["
					+ textyle.domain
					+ "]]></vid>\n</params>\n</methodCall>";

			KarybuHost.getINSTANCE().postRequest("/index.php", xmlForDelete);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();
			// finish();
		}

	}

	// async task for publishing the post

	// publish = save + publish
	private class PublishPostAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			// firstly, the post is saved
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall><params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
					+ "<vid><![CDATA["
					+ textyle.domain
					+ "]]></vid>\n"
					+ "<publish><![CDATA[N]]></publish>\n"
					+ "<_filter><![CDATA[save_post]]></_filter>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<title><![CDATA["
					+ titleEditText.getText().toString()
					+ "]]></title>\n"
					+ getAliasXmlString()
					+ "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
					+ "<content><![CDATA[<p>"
					+ htmlEditor.getContent()
					+ "</p>]]></content>\n"
					+ "<document_srl><![CDATA["
					+ document_srl
					+ "]]></document_srl>\n"
					+ "<editor_sequence><![CDATA["
					+ document_srl
					+ "]]></editor_sequence>\n"
					+ "<module><![CDATA[textyle]]></module>\n"
					+ "</params>\n</methodCall>\n";

			KarybuHost.getINSTANCE().postRequest("/index.php", xmlForSaving);

			// the xml for publishing
			String xmlForPublishing = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall><params><_filter><![CDATA[publish_post]]></_filter>"
					+ "<act><![CDATA[procTextylePostPublish]]></act>"
					+ "<document_srl><![CDATA["
					+ document_srl
					+ "]]></document_srl>"
					+ "<mid><![CDATA[textyle]]></mid>"
					+ "<vid><![CDATA["
					+ textyle.domain
					+ "]]></vid>"
					+ "<category_srl><![CDATA["
					+ category_srl
					+ "]]></category_srl>"
					+ "<trackback_charset><![CDATA[UTF-8]]></trackback_charset>"
					+ "<use_alias><![CDATA[N]]></use_alias>"
					+ "<allow_comment><![CDATA[Y]]></allow_comment>"
					+ "<allow_trackback><![CDATA[Y]]></allow_trackback>"
					+ "<subscription><![CDATA[N]]></subscription>"
					+ "<module><![CDATA[textyle]]></module></params></methodCall>";

			KarybuHost.getINSTANCE().postRequest("/index.php", xmlForPublishing);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();
		}

	}
}