package com.arnia.karybu.pages;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.arnia.karybu.R;
import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuPage;

public class PageController extends KarybuFragment implements
		OnClickListener, OnItemClickListener {
	private ListView listView;
	private PageAdapter adapter;
	private KarybuArrayList list;
	private Button addPageButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_page, container,
				false);

		listView = (ListView) view.findViewById(R.id.PAGE_LISTVIEW);
		addPageButton = (Button) view
				.findViewById(R.id.PAGE_ADDBUTTON);
		addPageButton.setOnClickListener(this);

		adapter = new PageAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// send request to get pages
		KarybuFragment.startProgress(getActivity(), "Loading...");
		GetPagesAsyncTask asyncRequest = new GetPagesAsyncTask();
		asyncRequest.execute();

	}

	// method called when one of the
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		KarybuPage page = list.pages.get(position);
		if (page.page_type.equals("ARTICLE")) {
			EditPageController pageEditor = new EditPageController();
			Bundle args = new Bundle();
			args.putString("mid", page.mid);
			args.putString("document_srl", page.document_srl);
			pageEditor.setArguments(args);
			MainActivityController mainActivity = (MainActivityController) this.activity;
			mainActivity.addMoreScreen(pageEditor);
		} else if (page.page_type.equals("WIDGET")) {
			Intent intent = new Intent(this.activity,
					WidgetController.class);
			intent.putExtra("mid", page.mid);
			if (page.virtual_site != null) {
				intent.putExtra("vid", page.virtual_site);
			}
			startActivity(intent);

		}
	}

	// called when a button in listview is pressed
	@Override
	public void onClick(View v) {

		// delete button pressed
		if (v.getId() == R.id.PAGEITEMCELL_DELETEBUTTON) {
			final int index = (Integer) v.getTag();
			new AlertDialog.Builder(activity)
					.setTitle("Attention")
					.setCancelable(false)
					.setMessage("Do you want to delete this page?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									// page where the user clicked
									KarybuPage page = list.pages.get(index);

									KarybuFragment.startProgress(activity,
											"Deleting...");
									DeletePageAsyncTask task = new DeletePageAsyncTask();
									task.execute(new String[] { page.module_srl });
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}

							}).create().show();

		} else if (v.getId() == R.id.PAGE_ADDBUTTON) {
			MainActivityController mainActivity = (MainActivityController) activity;
			mainActivity.addMoreScreen(new AddPageController());
		}
	}

	// AsyncTask that deletes a page
	private class DeletePageAsyncTask extends AsyncTask<String, Object, Object> {

		@Override
		protected Object doInBackground(String... param) {
			String moduleSRL = param[0];

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("ruleset", "deletePage");
			params.put("module", "page");
			params.put("act", "procPageAdminDelete");
			params.put("module_srl", moduleSRL);

			KarybuHost.getINSTANCE().postMultipart(params, "/");

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();
			startProgress(activity, "Loading...");
			GetPagesAsyncTask task = new GetPagesAsyncTask();
			task.execute();
		}
	}

	// AsyncTask that makes a request to obtain all pages
	private class GetPagesAsyncTask extends AsyncTask<Object, Object, Object> {
		String xmlData;

		@Override
		protected Object doInBackground(Object... params) {
			xmlData = KarybuHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationDisplayPages");

			Serializer serializer = new Persister();

			Reader reader = new StringReader(xmlData);
			try {
				list = serializer.read(KarybuArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			dismissProgress();
			if (list != null && list.pages != null) {
				adapter.setArrayWithPages(list.pages);
				adapter.notifyDataSetChanged();
			}
		}

	}

}
