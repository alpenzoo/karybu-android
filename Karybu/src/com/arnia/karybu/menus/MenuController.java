package com.arnia.karybu.menus;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuMenu;
import com.arnia.karybu.controls.KarybuDialog;

public class MenuController extends KarybuFragment implements
		OnItemClickListener, OnClickListener {

	private KarybuArrayList arrayWithMenus;
	private MenuAdapter adapter;
	private Button addMenuButton;

	public KarybuArrayList getArrayWithMenus() {
		return arrayWithMenus;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_menu, container, false);

		ListView list = (ListView) view.findViewById(R.id.MENU_LISTVIEW);
		addMenuButton = (Button) view.findViewById(R.id.MENU_ADDBUTTON);

		adapter = new MenuAdapter(this.activity);

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		addMenuButton.setOnClickListener(this);

		KarybuFragment
				.startProgress(getActivity(), getString(R.string.loading));
		GetMenusAsyncTask getAsyncRequest = new GetMenusAsyncTask();
		getAsyncRequest.execute();

		return view;
	}

	// called when an item from list is pressed
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		KarybuMenu menu = adapter.getArrayWithMenus().get(position);
		MainActivityController mainActivity = (MainActivityController) activity;
		MenuItemsController menuItemController = new MenuItemsController();
		Bundle argument = new Bundle();
		argument.putString("menu_srl", menu.menuSrl);
		argument.putString("menu_item_parent_srl", "0");
		menuItemController.setArguments(argument);
		mainActivity.addMoreScreen(menuItemController);
	}

	// called when Add menu button or Delete button are pressed
	@Override
	public void onClick(View v) {
		// Add button
		if (v.getId() == R.id.MENU_ADDBUTTON) {
			final KarybuDialog dialog = new KarybuDialog(activity);
			dialog.setTitle(R.string.new_menu);

			final EditText txtMenuName = new EditText(activity);
			txtMenuName.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			txtMenuName.setBackgroundResource(R.drawable.bg_edittext);
			txtMenuName
					.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
			txtMenuName.setHint(getString(R.string.menu_name));
			dialog.setView(txtMenuName);
			dialog.setPositiveButton(R.string.ok, new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.cancel();
					KarybuFragment.startProgress(getActivity(),
							"Saving menu...");
					AddMenuAsyncTask addMenuAT = new AddMenuAsyncTask();
					addMenuAT.execute(txtMenuName.getText().toString());
					dialog.dismiss();
				}
			});
			dialog.setNegativeButton(R.string.cancel);
			dialog.show();
		}
		// delete button
		else {
			Button deleteButtonPressed = (Button) v;
			int position = (Integer) deleteButtonPressed.getTag();
			KarybuMenu menu = this.arrayWithMenus.menus.get(position);

			DeleteMenuAsyncTask task = new DeleteMenuAsyncTask();
			String[] params = { menu.menuSrl, menu.menuName };
			task.execute(params);
		}
	}

	// async task that deletes a menu
	private class DeleteMenuAsyncTask extends AsyncTask<String, Object, Object> {

		@Override
		protected String doInBackground(String... param) {
			String menuSRL = param[0];
			String menuName = param[1];
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("menu_srl", menuSRL);
			params.put("title", menuName);
			KarybuHost
					.getINSTANCE()
					.postMultipart(params,
							"/index.php?module=mobile_communication&act=procmobile_communicationMenuDelete");
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			GetMenusAsyncTask task = new GetMenusAsyncTask();
			task.execute();
			adapter.setArrayWithMenus(arrayWithMenus.menus);
			adapter.notifyDataSetChanged();
		}
	}

	// async task that adds a menu
	private class AddMenuAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... param) {
			String name = param[0];
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("title", name);
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationMenuInsert");
			KarybuHost.getINSTANCE().postMultipart(params, "/");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			GetMenusAsyncTask asyncTask = new GetMenusAsyncTask();
			asyncTask.execute();
		}
	}

	// async task that sends a request for menus
	private class GetMenusAsyncTask extends AsyncTask<Object, Object, Object> {
		String xmlData;

		// send the request
		@Override
		protected Object doInBackground(Object... params) {
			xmlData = KarybuHost
					.getINSTANCE()
					.getRequest(
							"/index.php?XDEBUG_SESSION_START=netbeans-xdebug&module=mobile_communication&act=procmobile_communicationDisplayMenu");

			Serializer serializer = new Persister();

			Reader reader = new StringReader(xmlData);
			try {
				arrayWithMenus = serializer.read(KarybuArrayList.class, reader,
						false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		// when the response is received update the adapter
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();

			// isLoggedIn(xmlData, MenuController.this);

			if (arrayWithMenus != null && arrayWithMenus.menus != null) {
				// send the array with menus to adapter and notify that the data
				// has changed
				adapter.setArrayWithMenus(arrayWithMenus.menus);
				adapter.notifyDataSetChanged();
			}
		}

	}
}
