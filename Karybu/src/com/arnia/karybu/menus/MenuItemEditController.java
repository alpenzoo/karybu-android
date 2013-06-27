package com.arnia.karybu.menus;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuMenuItemsDetails;
import com.arnia.karybu.classes.KarybuModule;

public class MenuItemEditController extends KarybuFragment implements
		OnClickListener, android.widget.CompoundButton.OnCheckedChangeListener {
	// interface elements
	private EditText linkTitle;
	private RadioButton createRadioOption;
	private RadioButton selectRadioOption;
	private RadioButton menuURLRadioOption;

	private RadioButton articleRadioOption;
	private RadioButton widgetRadioOption;
	private RadioButton externalRadioOption;

	private TextView moduleIDTextView;
	private EditText moduleIDEditText;
	private CheckBox isNewWindow;

	private TextView menuURLTextView;
	private EditText menuURLEditText;

	private Spinner availablePages;
	private Spinner pageTypes;
	private Button saveButton;

	private String menuSRL;
	private String menuItemSRL;
	private String menuParentSrl;

	private KarybuMenuItemsDetails details;

	// Array with modules for spinner
	private KarybuArrayList modules;

	// spinner adapter
	private ArrayAdapter<KarybuModule> adapter;

	private final String PAGE_TYPE_WIDGET = "WIDGET";
	private final String PAGE_TYPE_EXTERNAL = "EXTERNAL";
	private final String PAGE_TYPE_ARTICAL = "ARTICLE";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_edit_menu_item, container,
				false);
		adapter = new ArrayAdapter<KarybuModule>(this.activity,
				android.R.layout.simple_spinner_item);

		availablePages = (Spinner) view.findViewById(R.id.AVAILABLE_PAGES);
		pageTypes = (Spinner) view.findViewById(R.id.PAGE_TYPES);

		linkTitle = (EditText) view.findViewById(R.id.LINK_TEXT);
		isNewWindow = (CheckBox) view.findViewById(R.id.NEW_WINDOW);

		Bundle args = getArguments();
		menuSRL = args.getString("menu_srl");
		menuItemSRL = args.getString("menu_item_srl");
		menuParentSrl = args.getString("menu_parent_srl");

		// make request to get a list of modules for spinner
		GetModulesAsyncTask task = new GetModulesAsyncTask();
		task.execute();

		// action for save button
		saveButton = (Button) view.findViewById(R.id.EDIT_MENU_SAVE_BUTTON);
		saveButton.setOnClickListener(this);

		availablePages.setAdapter(adapter);

		// handle when selected page type
		pageTypes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				refreshAvailablePageAdapter(returnType());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		return view;
	}

	public void refreshAvailablePageAdapter(String pageType) {
		if (modules != null) {
			if (modules.modules != null) {
				KarybuModule modulePage;
				adapter.clear();
				for (int i = 0; i < modules.modules.size(); i++) {
					modulePage = modules.modules.get(i);
					if (modulePage.page_type.compareTo(pageType) == 0) {
						adapter.add(modulePage);
					}
				}
				adapter.notifyDataSetChanged();
			}
		}
	}

	// called when the save button is pressed
	@Override
	public void onClick(View v) {
		SaveMenuItemAsyncTask task = new SaveMenuItemAsyncTask();
		task.execute();
	}

	// the method returns the type selected
	private String returnType() {
		if (((String) pageTypes.getSelectedItem()).compareTo("Widget page") == 0) {
			return this.PAGE_TYPE_WIDGET;
		} else if (((String) pageTypes.getSelectedItem())
				.compareTo("Article page") == 0) {
			return this.PAGE_TYPE_ARTICAL;
		} else {
			return this.PAGE_TYPE_EXTERNAL;
		}

	}

	private String getPageTypeValue(String text) {
		if (text.compareTo("Widget page") == 0) {
			return this.PAGE_TYPE_WIDGET;
		} else if (text.compareTo("Article page") == 0) {
			return this.PAGE_TYPE_ARTICAL;
		} else {
			return this.PAGE_TYPE_EXTERNAL;
		}
	}

	// update the interface when the user change the option
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == menuURLRadioOption) {
			if (isChecked) {
				availablePages.setVisibility(View.INVISIBLE);
				articleRadioOption.setVisibility(View.INVISIBLE);
				widgetRadioOption.setVisibility(View.INVISIBLE);
				externalRadioOption.setVisibility(View.INVISIBLE);

				moduleIDEditText.setVisibility(View.INVISIBLE);
				moduleIDTextView.setVisibility(View.INVISIBLE);

				menuURLTextView.setVisibility(View.VISIBLE);
				menuURLEditText.setVisibility(View.VISIBLE);
			} else {
				menuURLTextView.setVisibility(View.INVISIBLE);
				menuURLEditText.setVisibility(View.INVISIBLE);
			}
		} else if (buttonView == createRadioOption) {
			if (isChecked) {
				articleRadioOption.setVisibility(View.VISIBLE);
				articleRadioOption.setChecked(true);
				widgetRadioOption.setVisibility(View.VISIBLE);
				externalRadioOption.setVisibility(View.VISIBLE);

				moduleIDEditText.setVisibility(View.VISIBLE);
				moduleIDTextView.setVisibility(View.VISIBLE);

				menuURLTextView.setVisibility(View.INVISIBLE);
				menuURLEditText.setVisibility(View.INVISIBLE);

				availablePages.setVisibility(View.VISIBLE);
			} else {
				articleRadioOption.setVisibility(View.INVISIBLE);
				widgetRadioOption.setVisibility(View.INVISIBLE);
				externalRadioOption.setVisibility(View.INVISIBLE);

				moduleIDEditText.setVisibility(View.INVISIBLE);
				moduleIDTextView.setVisibility(View.INVISIBLE);
			}
		} else if (buttonView == selectRadioOption) {
			if (isChecked) {
				articleRadioOption.setVisibility(View.INVISIBLE);
				widgetRadioOption.setVisibility(View.INVISIBLE);
				externalRadioOption.setVisibility(View.INVISIBLE);

				moduleIDEditText.setVisibility(View.INVISIBLE);
				moduleIDTextView.setVisibility(View.INVISIBLE);

				menuURLTextView.setVisibility(View.INVISIBLE);
				menuURLEditText.setVisibility(View.INVISIBLE);

				availablePages.setVisibility(View.VISIBLE);
			} else {
				availablePages.setVisibility(View.INVISIBLE);
			}
		}
	}

	// Async Task that gets details about the current edited menu
	private class GetEditedMenuAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String xmlData = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><methodCall><params><menu_item_srl><![CDATA["
					+ menuItemSRL
					+ "]]></menu_item_srl><module><![CDATA[menu]]></module><act><![CDATA[getMenuAdminItemInfo]]></act></params></methodCall>";

			String response = KarybuHost.getINSTANCE().postRequest(
					"/index.php", xmlData);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);

			try {
				details = serializer.read(KarybuMenuItemsDetails.class, reader,
						false);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			linkTitle.setText(details.name);
			if (details.open_window.equals("Y"))
				isNewWindow.setChecked(true);
			else
				isNewWindow.setChecked(false);

			// moduleType may be null somehow
			if (details.moduleType == null) {
				// createRadioOption.setChecked(true);
			} else {
				if (details.moduleType.equals("page")) {

					int i;
					for (i = 0; i < pageTypes.getAdapter().getCount() - 1; i++) {
						if ((getPageTypeValue((String) pageTypes.getAdapter()
								.getItem(i))).compareTo(details.pageType) == 0) {
							pageTypes.setSelection(i);
							break;
						}
					}

					refreshAvailablePageAdapter(details.pageType);

					// select the correct page in spinner

					for (i = 0; i < adapter.getCount() - 1; i++) {

						// if( details.url.equals(modules.modules.get(i).module)
						// ) break;
						if (details.url.equals(adapter.getItem(i).module))
							break;
					}
					Log.d("i=", i + " ");
					availablePages.setSelection(i);

				} else if (details.moduleType.equals("url")) {
					Log.d("ajunge aici", "dada");
					menuURLRadioOption.setChecked(true);

					menuURLEditText.setText(details.url);
				}
			}

		}

	}

	// Async Task to get a list of modules for the spinner adapter
	private class GetModulesAsyncTask extends AsyncTask<Object, Object, Object> {

		String xmlData;

		@Override
		protected Object doInBackground(Object... params) {
			xmlData = KarybuHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationListModules");

			Serializer serializer = new Persister();

			Reader reader = new StringReader(xmlData);
			try {
				modules = serializer.read(KarybuArrayList.class, reader, false);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			// isLoggedIn(xmlData, MenuItemEditController.this);

			if (modules != null && modules.modules != null) {
				refreshAvailablePageAdapter(returnType());

				GetEditedMenuAsyncTask editedMenuTask = new GetEditedMenuAsyncTask();
				editedMenuTask.execute();

				Log.i("Finish loading", "Modules");
			}

		}
	}

	private class SaveMenuItemAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(activity,
					getString(R.string.processing));
		}

		@Override
		protected Object doInBackground(Object... param) {
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("ruleset", "insertMenuItem");
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationMenuItem");
			params.put("menu_srl", menuSRL);
			params.put("menu_item_srl", menuItemSRL);
			params.put("menu_parent_srl", menuParentSrl);
			params.put("menu_name_key", linkTitle.getText().toString());
			params.put("menu_name", linkTitle.getText().toString());
			params.put("cType", "SELECT");
			params.put("module_type", returnType());
			params.put("menu_open_window", openInNewWindow());
			params.put("select_menu_url", availablePages.getSelectedItem()
					.toString());

			KarybuHost.getINSTANCE().postMultipart(params,
					"/?XDEBUG_SESSION_START=netbeans-xdebug");
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
			((MainActivityController) activity).backwardScreen();
		}

		private String openInNewWindow() {
			if (isNewWindow.isChecked())
				return "Y";
			else
				return "N";
		}

	}

}
