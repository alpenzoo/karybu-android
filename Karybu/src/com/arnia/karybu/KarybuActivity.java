package com.arnia.karybu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//Activity that has a ProgressDialog and one method that verifies if the user is logged id
public class KarybuActivity extends Activity {
	protected ProgressDialog progress;

	public void startProgress(String message) {
		progress = ProgressDialog.show(this, null, message, true, false);
	}

	public void dismissProgress() {
		progress.dismiss();
	}

	// if the user is logged out the Login Controller is pushed
	public boolean isLoggedIn(String response, Context context) {
		if (response.equals("logout_error!")) {
			Toast.makeText(this, R.string.session_expired_msg,
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(context, LoginController.class);
			startActivity(intent);

			return false;
		}

		return true;
	}
}
