package com.arnia.karybu;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.arnia.karybu.classes.KarybuTextyle;
import com.arnia.karybu.data.KarybuSite;

public class KarybuFragment extends Fragment {

	protected FragmentActivity activity;
	protected ActionBar actionBar;
	private static int progressDialogCount;
	private static ProgressDialog progress;

	public static void startProgress(Context context, String message) {
		progressDialogCount++;
		if (progressDialogCount == 1)
			progress = ProgressDialog.show(context, null, message, true, false);
	}

	public static void dismissProgress() {
		progressDialogCount--;
		if (progressDialogCount <= 0 && progress != null) {
			progress.dismiss();
			progressDialogCount = 0;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		activity = getActivity();
		actionBar = activity.getActionBar();
		super.onCreate(savedInstanceState);
	}

	public void addNestedFragment(int layoutID, Fragment fragment,
			String fragmentName) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.add(layoutID, fragment, fragmentName).commit();
	}

	protected void onSelectedSite(KarybuSite site) {

	}

	protected void onSelectedTextyle(KarybuTextyle textyle) {

	}

}
