package com.arnia.karybu;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuResponse;
import com.arnia.karybu.controls.KarybuDialog;
import com.arnia.karybu.data.KarybuDatabaseHelper;
import com.google.android.gcm.GCMRegistrar;

public class LoginController extends KarybuActivity implements OnClickListener {

	private EditText addressEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);

		// take reference to the UI elements
		addressEditText = (EditText) findViewById(R.id.LOGIN_ADDRESS);
		usernameEditText = (EditText) findViewById(R.id.LOGIN_USERNAME);
		passwordEditText = (EditText) findViewById(R.id.LOGIN_PASSWORD);

		Button loginButton = (Button) findViewById(R.id.LOGIN_BUTTON);
		loginButton.setOnClickListener(this);

	}

	// called when login button is pressed
	@Override
	public void onClick(View v) {
		String websiteUrl = addressEditText.getText().toString().trim();
		String username = usernameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();

		// Validate input
		if (websiteUrl.length() == 0 || username.length() == 0
				|| password.length() == 0) {
			final KarybuDialog invalidInputDialog = new KarybuDialog(
					LoginController.this);
			invalidInputDialog.setIcon(R.drawable.ic_warning);
			invalidInputDialog.setTitle(R.string.login_invalid_input_title);
			invalidInputDialog.setMessage(R.string.login_invalid_input_msg);
			invalidInputDialog.setPositiveButton(getString(R.string.close));
			invalidInputDialog.show();
		} else {
			websiteUrl = getURL(websiteUrl);
			LogInInBackground task = new LogInInBackground();
			task.execute(websiteUrl, username, password);
		}
	}

	private String getURL(String url) {
		if (!url.contains("http://"))
			url = "http://" + url;
		if (url.substring(url.length() - 1).equals("/"))
			url = url.substring(0, url.length() - 1);
		return url;
	}

	// AsyncTask for LogIn
	private class LogInInBackground extends AsyncTask<String, Void, Void> {

		private String websiteUrl;
		private String username;
		private String password;

		private String xmlData;
		private boolean request_url_error = false;
		private KarybuDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new KarybuDialog(LoginController.this);
			dialog.setTitle(R.string.checking_connection_dialog_title);
			dialog.setMessage(R.string.checking_connection_dialog_description);
			dialog.show();
		}

		// send the request in background
		@Override
		protected Void doInBackground(String... params) {
			websiteUrl = params[0];
			username = params[1];
			password = params[2];
			try {
				// set address in KarybuHost singleton
				KarybuHost.getINSTANCE().setURL(websiteUrl);

				xmlData = KarybuHost
						.getINSTANCE()
						.postRequest(
								"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ username + "&password=" + password, "");

			} catch (Exception e) {
				e.printStackTrace();
				request_url_error = true;
			}
			return null;
		}

		// verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();

			if (!request_url_error && xmlData != null) {
				try {
					// parse the response
					Serializer serializer = new Persister();

					Reader reader = new StringReader(xmlData);
					KarybuResponse response = serializer.read(
							KarybuResponse.class, reader, false);

					registerForPushNotification();

					// check if the response was positive
					if (response.value.equals("true")) {
						// Write site data to database
						KarybuDatabaseHelper dbHelper = KarybuDatabaseHelper
								.getDBHelper(getApplicationContext());
						SQLiteDatabase db = dbHelper.getReadableDatabase();
						String[] args = { websiteUrl };
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
							values.put(dbHelper.KARYBU_SITES_SITEURL,
									websiteUrl);
							values.put(dbHelper.KARYBU_SITES_PASSWORD, password);
							values.put(dbHelper.KARYBU_SITES_USERNAME, username);
							db.insert(dbHelper.KARYBU_SITES, null, values);

							db.close();
						}

						// call dash board activity
						Intent callDashboard = new Intent(LoginController.this,
								MainActivityController.class);
						startActivity(callDashboard);
						finish();
						return;
					} else {
						// Alert wrong password
						dialog = new KarybuDialog(LoginController.this);
						dialog.setIcon(R.drawable.ic_warning);
						dialog.setTitle(R.string.wrong_password_dialog_title);
						dialog.setMessage(R.string.wrong_password_dialog_description);
						dialog.setPositiveButton(getString(R.string.close));
						dialog.show();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			dialog = new KarybuDialog(LoginController.this);
			dialog.setIcon(R.drawable.ic_warning);
			dialog.setTitle(R.string.error);
			dialog.setMessage(R.string.login_error_msg);
			dialog.setPositiveButton(getString(R.string.close));
			dialog.show();
			return;
		}

	}

	private void registerForPushNotification() {
		if (GCMRegistrar.isRegistered(this)) {
			Log.d("info", GCMRegistrar.getRegistrationId(this));
		}

		final String regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) {
			// replace this with the project ID
			GCMRegistrar.register(this, "946091851170");
			Log.d("info", GCMRegistrar.getRegistrationId(this));
		} else {
			Log.d("info", "already registered as" + regId);
		}
	}
}
