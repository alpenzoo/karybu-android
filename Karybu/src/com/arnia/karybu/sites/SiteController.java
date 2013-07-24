package com.arnia.karybu.sites;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuResponse;
import com.arnia.karybu.controls.KarybuDialog;
import com.arnia.karybu.data.KarybuDatabaseHelper;
import com.arnia.karybu.data.KarybuSite;
import com.arnia.karybu.utilities.CommonUtils;

public class SiteController extends KarybuFragment implements OnClickListener {

	private ListView lvwSite;
	private SiteAdapter adapter;
	private Button btnAdd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.layout_site, container,
				false);

		lvwSite = (ListView) fragmentView.findViewById(R.id.SITE_LISTVIEW);
		KarybuDatabaseHelper dbHelper = KarybuDatabaseHelper
				.getDBHelper(this.activity);
		adapter = new SiteAdapter(getActivity(), dbHelper.getAllSites());

		lvwSite.setAdapter(adapter);

		btnAdd = (Button) fragmentView.findViewById(R.id.SITE_ADD_BUTTON);
		btnAdd.setOnClickListener(this);

		return fragmentView;
	}

	@Override
	public void onClick(View v) {

		final KarybuDialog dialog = new KarybuDialog(activity);
		dialog.setTitle(R.string.new_website);

		final LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10, 0, 10, 0);

		final EditText txtUrl = new EditText(getActivity());
		txtUrl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
		txtUrl.setBackgroundResource(R.drawable.bg_edittext);
		txtUrl.setHint(R.string.login_hint_website_url);

		final EditText txtUsername = new EditText(getActivity());
		txtUsername.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtUsername.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		txtUsername.setBackgroundResource(R.drawable.bg_edittext);
		txtUsername.setHint(R.string.login_hint_username);

		final EditText txtPassword = new EditText(getActivity());
		txtPassword.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		txtPassword.setBackgroundResource(R.drawable.bg_edittext);
		txtPassword.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		txtPassword.setHint(R.string.login_hint_password);

		layout.addView(txtUrl);
		layout.addView(txtUsername);
		layout.addView(txtPassword);

		dialog.setView(layout);
		dialog.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = txtUrl.getText().toString().trim();
				String username = txtUsername.getText().toString().trim();
				String password = txtPassword.getText().toString().trim();
				if (url.length() == 0 || username.length() == 0
						|| password.length() == 0) {
					Toast.makeText(activity, getString(R.string.invalid_input),
							Toast.LENGTH_LONG).show();
				} else {
					url = CommonUtils.getValidUrl(url);
					KarybuSite site = new KarybuSite(0, url, username, password);
					AddSiteBackground task = new AddSiteBackground();
					task.execute(site);
					dialog.dismiss();
				}
			}
		});
		dialog.setNegativeButton(R.string.cancel);
		dialog.show();
	}

	private class AddSiteBackground extends AsyncTask<KarybuSite, Void, Void> {
		String xmlData;
		KarybuSite site;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(activity, "Validating...");
		}

		// send the request in background
		@Override
		protected Void doInBackground(KarybuSite... params) {
			site = params[0];
			try {
				// set address in KarybuHost singleton
				KarybuHost.getINSTANCE().setURL(site.siteUrl);

				xmlData = KarybuHost
						.getINSTANCE()
						.postRequest(
								"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ site.userName
										+ "&password="
										+ site.password);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;

		}

		// verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// when the response came, remove the loading message
			KarybuFragment.dismissProgress();

			try {
				// parse the response
				Serializer serializer = new Persister();

				Reader reader = new StringReader(xmlData);
				KarybuResponse response = serializer.read(KarybuResponse.class,
						reader, false);

				// check if the response was positive
				if (response.value.equals("true")) {
					// Write site data to database
					KarybuDatabaseHelper dbHelper = KarybuDatabaseHelper
							.getDBHelper(getActivity());
					SQLiteDatabase db = dbHelper.getReadableDatabase();
					String[] args = { site.siteUrl };
					Cursor cursor = db.rawQuery(
							"SELECT count(*) countUrl FROM "
									+ dbHelper.KARYBU_SITES + " WHERE "
									+ dbHelper.KARYBU_SITES_SITEURL + "=?",
							args);
					cursor.moveToFirst();
					int urlCount = cursor.getInt(0);
					cursor.close();
					db.close();
					if (urlCount == 0) {
						db = dbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(dbHelper.KARYBU_SITES_SITEURL, site.siteUrl);
						values.put(dbHelper.KARYBU_SITES_PASSWORD,
								site.password);
						values.put(dbHelper.KARYBU_SITES_USERNAME,
								site.userName);
						db.insert(dbHelper.KARYBU_SITES, null, values);
						db.close();
					}
					adapter.addItem(site);
				} else {
					Toast.makeText(getActivity(),
							"Incorrect username or password!",
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(),
						"Error while trying to add new website",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
