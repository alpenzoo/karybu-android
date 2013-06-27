package com.arnia.karybu.sites;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuResponse;
import com.arnia.karybu.controls.KarybuDialog;
import com.arnia.karybu.data.KarybuDatabaseHelper;
import com.arnia.karybu.data.KarybuSite;

public class SiteAdapter extends BaseAdapter {
	// array with pages that appear in listview
	private ArrayList<KarybuSite> sites;

	private Activity context;

	public SiteAdapter(Activity context) {
		sites = new ArrayList<KarybuSite>();
		this.context = context;
	}

	public SiteAdapter(Activity context, ArrayList<KarybuSite> sites) {
		this.sites = sites;
		this.context = context;
	}

	@Override
	public int getCount() {
		return sites.size();
	}

	@Override
	public Object getItem(int index) {
		return sites.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// get the page from the array
		final KarybuSite site = sites.get(pos);
		final int position = pos;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.cellview_site, null);
		}

		// construct the view's elements
		Button btnEdit = (Button) convertView
				.findViewById(R.id.SITE_EDIT_BUTTON);
		Button btnDelete = (Button) convertView
				.findViewById(R.id.SITE_DELETE_BUTTON);

		TextView txtUrl = (TextView) convertView.findViewById(R.id.SITE_URL);
		txtUrl.setText(site.siteUrl);

		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final KarybuDialog dialog = new KarybuDialog(context);
				dialog.setTitle(R.string.edit_website);

				final LinearLayout layout = new LinearLayout(context);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setPadding(10, 0, 10, 0);

				final EditText txtUrl = new EditText(context);
				txtUrl.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
				txtUrl.setBackgroundResource(R.drawable.bg_edittext);
				txtUrl.setHint(R.string.login_hint_website_url);
				txtUrl.setText(site.siteUrl);

				final EditText txtUsername = new EditText(context);
				txtUsername.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtUsername
						.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				txtUsername.setBackgroundResource(R.drawable.bg_edittext);
				txtUsername.setHint(R.string.login_hint_username);
				txtUsername.setText(site.userName);

				final EditText txtPassword = new EditText(context);
				txtPassword.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtPassword
						.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
				txtPassword.setBackgroundResource(R.drawable.bg_edittext);
				txtPassword
						.setTransformationMethod(PasswordTransformationMethod
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
						String username = txtUsername.getText().toString()
								.trim();
						String password = txtPassword.getText().toString()
								.trim();
						KarybuSite editedSite = new KarybuSite(site.id, url,
								username, password);
						LogInInBackground task = new LogInInBackground(position);
						task.execute(editedSite);
						dialog.dismiss();
					}
				});
				dialog.setNegativeButton(R.string.cancel);
				dialog.show();
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final KarybuDialog dialog = new KarybuDialog(context);
				dialog.setTitle(R.string.delete_website);
				dialog.setMessage(R.string.delete_website_msg);
				dialog.setPositiveButton(R.string.yes, new OnClickListener() {

					@Override
					public void onClick(View v) {
						KarybuDatabaseHelper dbHelper = KarybuDatabaseHelper
								.getDBHelper(context);
						SQLiteDatabase db = dbHelper.getReadableDatabase();
						db.delete(dbHelper.KARYBU_SITES,
								dbHelper.KARYBU_SITES_ID + "=" + site.id, null);
						sites.remove(site);
						notifyDataSetChanged();
						dialog.dismiss();
					}
				});
				dialog.setNegativeButton(R.string.no);
				dialog.show();

			}
		});

		return convertView;
	}

	public void addItem(KarybuSite site) {
		sites.add(site);
	}

	private class LogInInBackground extends AsyncTask<KarybuSite, Void, Void> {
		private String xmlData;
		private KarybuSite site;
		private int index;

		public LogInInBackground(int index) {
			this.index = index;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(context, "Validating...");
		}

		// send the request in background
		@SuppressWarnings("finally")
		@Override
		protected Void doInBackground(KarybuSite... params) {
			site = params[0];
			try {
				// set address in KarybuHost singleton
				KarybuHost.getINSTANCE().setURL(site.siteUrl);

				xmlData = KarybuHost
						.getINSTANCE()
						.getRequest(
								"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ site.userName
										+ "&password="
										+ site.password);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				return null;
			}
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
							.getDBHelper(context);
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
					if (urlCount == 1) {
						db = dbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(dbHelper.KARYBU_SITES_SITEURL, site.siteUrl);
						values.put(dbHelper.KARYBU_SITES_PASSWORD,
								site.password);
						values.put(dbHelper.KARYBU_SITES_USERNAME,
								site.userName);
						db.update(dbHelper.KARYBU_SITES, values,
								dbHelper.KARYBU_SITES_ID + "=" + site.id, null);
						db.close();
						sites.set(index, site);
						notifyDataSetChanged();
					}
				} else {
					Toast toast = Toast.makeText(context,
							"Incorrect username or password!",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
