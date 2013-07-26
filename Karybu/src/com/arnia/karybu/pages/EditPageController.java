package com.arnia.karybu.pages;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

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
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuResponse;
import com.arnia.karybu.controls.KarybuTextEditor;
import com.arnia.karybu.utilities.CommonUtils;

public class EditPageController extends KarybuFragment implements
		OnClickListener {
	// private String mid;
	private String document_srl;
	private KarybuTextEditor htmlEditor;
	private EditText titleEditText;
	private Button saveButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_edit_page, container,
				false);
		htmlEditor = new KarybuTextEditor();

		addNestedFragment(R.id.TEXT_EDITOR_HOLDER, htmlEditor, "htmlTextEditor");

		Bundle args = getArguments();
		// mid = args.getString("mid");
		document_srl = args.getString("document_srl");

		saveButton = (Button) view.findViewById(R.id.PAGE_EDITOR_SAVE);
		saveButton.setOnClickListener(this);

		titleEditText = (EditText) view
				.findViewById(R.id.PAGE_EDITOR_BROWSER_TITLE);

		KarybuFragment.startProgress(getActivity(), "Page content is loading");
		GetPageContentAndTitleAsyncTask task = new GetPageContentAndTitleAsyncTask();
		task.execute();

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.PAGE_EDITOR_SAVE:
			SavePageAsyncTask task = new SavePageAsyncTask();
			task.execute();
			break;
		}
	}

	// Async Task that gets the page content and title
	private class GetPageContentAndTitleAsyncTask extends
			AsyncTask<Object, Object, Object> {
		String responseContent;
		String responseTitle;

		@Override
		protected Object doInBackground(Object... params) {
			responseContent = KarybuHost
					.getINSTANCE()
					.postRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationArticleContent&srl="
									+ document_srl);
			responseTitle = KarybuHost
					.getINSTANCE()
					.postRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationArticleTitle&srl="
									+ document_srl);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			responseTitle = responseTitle.replace("<br/>", "\n");
			responseContent = responseContent.replace("<br/>", "\n");

			titleEditText.setText(responseTitle);
			htmlEditor.setContent(responseContent);
			dismissProgress();
		}
	}

	// Async Task that saves the page content and title
	private class SavePageAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(activity,
					getString(R.string.processing));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String content = htmlEditor.getContent();
			String title = titleEditText.getText().toString();

			HashMap<String, String> ps = new HashMap<String, String>();
			ps.put("module", "mobile_communication");
			ps.put("act", "procmobile_communicationPageEdit");
			ps.put("title", title);
			ps.put("content", content);
			ps.put("document_srl", document_srl);
			ps.put("key", CommonUtils.getSha1("karybu-mobile-app"));

			try {
				String strResponse = KarybuHost.getINSTANCE().postMultipart(ps,
						"/");

				Serializer serializer = new Persister();
				Reader reader = new StringReader(strResponse);
				KarybuResponse response = serializer.read(KarybuResponse.class,
						reader);
				if (response.error == 0)
					return true;
				else
					return false;

			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
			if (result) {
				Toast.makeText(activity, "Page has been saved.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(activity, "Fail to save page.",
						Toast.LENGTH_LONG).show();
			}

		}

	}

}
