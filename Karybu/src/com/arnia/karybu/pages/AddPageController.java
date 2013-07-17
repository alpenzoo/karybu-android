package com.arnia.karybu.pages;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.arnia.karybu.R;
import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuLayout;
import com.arnia.karybu.classes.KarybuResponse;

public class AddPageController extends KarybuFragment implements
		OnClickListener {
	// UI references
	private Spinner pageType;
	private EditText pageName;
	private EditText browserTitle;
	private Spinner layoutSpinner;
	private Button saveButton;

	private View view;

	// adapter for the list with pages
	private ArrayAdapter<String> adapter;

	private KarybuArrayList listLayouts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.layout_add_page, container,
				false);

		pageName = (EditText) view.findViewById(R.id.ADDPAGE_PAGENAME);
		browserTitle = (EditText) view
				.findViewById(R.id.ADDPAGE_BROWSERTITLE);

		layoutSpinner = (Spinner) view
				.findViewById(R.id.ADDPAGE_LAYOUTSPINNER);
		adapter = new ArrayAdapter<String>(this.activity,
				android.R.layout.simple_spinner_item);

		saveButton = (Button) view
				.findViewById(R.id.ADDPAGE_SAVEBUTTON);

		saveButton.setOnClickListener(this);

		pageType = (Spinner) view
				.findViewById(R.id.ADDPAGE_PAGETYPESPINNER);

		GetLayoutsAsyncTask task = new GetLayoutsAsyncTask();
		task.execute();

		return view;
	}

	// method called when the save button is pressed
	@Override
	public void onClick(View v) {
		// start request to add the page
		KarybuFragment.startProgress(getActivity(), "Saving page...");
		AddPageAsyncTask task = new AddPageAsyncTask();
		task.execute();
	}

	// method that returns the type of the page
	public String getPageType() {
		String selectedPageType = (String) pageType.getItemAtPosition(pageType
				.getSelectedItemPosition());
		if (selectedPageType.compareTo("Widget page") == 0) {
			return "WIDGET";
		} else if (selectedPageType.compareTo("Article page") == 0) {
			return "ARTICLE";
		} else {
			return "EXTERNAL";
		}
	}

	// Async Task that adds a page
	private class AddPageAsyncTask extends AsyncTask<String, Object, Object> {

		String response;

		@Override
		protected Object doInBackground(String... param) {
			// building the request
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("error_return_url",
					"/index.php?module=admin&act=dispPageAdminInsert");
			params.put("ruleset", "insertPage");
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationPageInsert");
			params.put("page_type", getPageType());
			params.put("page_name", pageName.getText().toString());
			params.put("browser_title", browserTitle.getText().toString());
			params.put("skin", "default");
			params.put("mskin", "default");

			KarybuLayout layout = listLayouts.layouts.get(layoutSpinner
					.getSelectedItemPosition());
			params.put("layout_srl", layout.layout_srl);

			// sending the request
			response = KarybuHost.getINSTANCE().postMultipart(params, "/");

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			KarybuResponse responseConfirmation = null;
			dismissProgress();
			try {
				Serializer serializer = new Persister();

				Reader reader = new StringReader(response);
				responseConfirmation = serializer.read(KarybuResponse.class,
						reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (responseConfirmation.value.equals("true")) {
				// Redirect to page manager
				MainActivityController mainActivity = (MainActivityController) activity;
				mainActivity.addMoreScreen(new PageController());
			} else {
				new AlertDialog.Builder(activity)
						.setTitle("Attention")
						.setMessage(
								"This page is already existing. Please use different name and title.")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create().show();
			}
		}
	}

	// Async task that gets the list with Layouts
	private class GetLayoutsAsyncTask extends AsyncTask<Object, Object, Object> {
		String response;

		@Override
		protected Object doInBackground(Object... params) {
			// make request
			response = KarybuHost
					.getINSTANCE()
					.postRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationGetLayout");

			// parse response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try {
				listLayouts = serializer.read(KarybuArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			// add layouts to spinner adapter
			if (listLayouts != null && listLayouts.layouts != null) {
				for (int i = 0; i < listLayouts.layouts.size(); i++)
					adapter.add(listLayouts.layouts.get(i).layout_name);
				layoutSpinner.setAdapter(adapter);
			}

		}
	}

}
