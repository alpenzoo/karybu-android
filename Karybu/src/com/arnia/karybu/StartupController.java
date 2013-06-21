package com.arnia.karybu;

import com.arnia.karybu.data.KarybuDatabaseHelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class StartupController extends KarybuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		KarybuDatabaseHelper dbHelper = KarybuDatabaseHelper.getDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) totalWebsite from "
				+ dbHelper.KARYBU_SITES, null);
		cursor.moveToFirst();
		int recordCount = cursor.getInt(0);
		cursor.close();
		db.close();
		if (recordCount > 0) {
			startActivity(new Intent(this, MainActivityController.class));
		} else {
			Intent callAddNewSite = new Intent(this,
					WelcomeScreenController.class);
			startActivity(callAddNewSite);
		}
		finish();
	}
}