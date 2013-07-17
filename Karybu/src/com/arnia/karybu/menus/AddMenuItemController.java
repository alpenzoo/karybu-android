package com.arnia.karybu.menus;

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

public class AddMenuItemController extends KarybuFragment implements
		OnClickListener, android.widget.CompoundButton.OnCheckedChangeListener {
	// interface elements
	private EditText browserTitleEditText;
	private RadioButton createRadioOption;
	private RadioButton selectRadioOption;
	private RadioButton menuURLRadioOption;

	private RadioButton articleRadioOption;
	private RadioButton widgetRadioOption;
	private RadioButton externalRadioOption;

	private TextView moduleIDTextView;
	private EditText moduleIDEditText;
	private CheckBox newWindow;

	private TextView menuURLTextView;
	private EditText menuURLEditText;

	private Spinner pageType;
	private Spinner availablePages;
	private Button saveButton;

	// menu parent srl
	private String parentSRL;
	private String menuSRL;

	private ArrayAdapter<String> adapter;

	// Array with modules for spinner
	private KarybuArrayList modules;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_edit_menu_item, container,
				false);

		availablePages = (Spinner) view.findViewById(R.id.AVAILABLE_PAGES);
		pageType = (Spinner) view.findViewById(R.id.PAGE_TYPES);
		saveButton = (Button) view.findViewById(R.id.EDIT_MENU_SAVE_BUTTON);
		browserTitleEditText = (EditText) view.findViewById(R.id.LINK_TEXT);
		newWindow = (CheckBox) view.findViewById(R.id.NEW_WINDOW);

		Bundle args = getArguments();
		parentSRL = args.getString("menu_parent_srl");
		menuSRL = args.getString("menu_srl");

		// action for save button
		saveButton.setOnClickListener(this);

		adapter = new ArrayAdapter<String>(this.activity,
				android.R.layout.simple_spinner_item);

		GetModuleAsyncTask task = new GetModuleAsyncTask();
		task.execute();
		return view;
	}

	// called when the save button is pressed
	@Override
	public void onClick(View v) {
		AddMenuItemAsyncTask task = new AddMenuItemAsyncTask();
		task.execute();
	}

	private String returnType() {
		if (((String) pageType.getSelectedItem()).compareTo("Article page") == 0) {
			return "ARTICLE";
		} else if (((String) pageType.getSelectedItem())
				.compareTo("Widget page") == 0) {
			return "WIDGET";
		} else {
			return "EXTERNAL";
		}

	}

	private String openInNewWindow() {
		if (newWindow.isChecked())
			return "Y";
		else
			return "N";
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

	private class AddMenuItemAsyncTask extends
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
			params.put("parent_srl", parentSRL);
			params.put("menu_name_key", browserTitleEditText.getText()
					.toString());
			params.put("menu_name", browserTitleEditText.getText().toString());
			params.put("cType", "SELECT");
			params.put("module_type", returnType());
			params.put("menu_open_window", openInNewWindow());
			params.put("select_menu_url",
					(String) availablePages.getSelectedItem());

			KarybuHost.getINSTANCE().postMultipart(params, "/");
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
			((MainActivityController) activity).backwardScreen();
		}
	}

	// Async Task that gets a list of modules
	// the list is user for the module spinner
	private class GetModuleAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String xmlData = KarybuHost
					.getINSTANCE()
					.postRequest(
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

			// populate spinner with Modules
			for (int i = 0; i < modules.modules.size(); i++) {
				adapter.add(modules.modules.get(i).module);
			}

			availablePages.setAdapter(adapter);
			availablePages.setSelected(false);
		}
	}
}
