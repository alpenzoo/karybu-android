package com.arnia.karybu.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class KarybuDatabaseHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "karybu";

	private final static int VERSION = 1;

	private static KarybuDatabaseHelper dbHelper = null;

	public final String KARYBU_SITES = "karybu_sites";
	public final String KARYBU_SITES_ID = "_id";
	public final String KARYBU_SITES_SITEURL = "siteurl";
	public final String KARYBU_SITES_USERNAME = "username";
	public final String KARYBU_SITES_PASSWORD = "password";

	public static KarybuDatabaseHelper getDBHelper(Context context) {
		if (dbHelper == null) {
			dbHelper = new KarybuDatabaseHelper(context, DB_NAME, null, VERSION);
		}
		return dbHelper;
	}

	private KarybuDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DB_NAME, factory, VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql;
		sql = "CREATE TABLE "
				+ KARYBU_SITES
				+ "(_id integer auto increment,siteurl TEXT,username TEXT,password TEXT)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public ArrayList<KarybuSite> getAllSites() {
		ArrayList<KarybuSite> sites = new ArrayList<KarybuSite>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + KARYBU_SITES
				+ " ORDER BY _id DESC", null);
		while (cursor.moveToNext()) {
			sites.add(new KarybuSite(cursor.getLong(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3)));
		}
		cursor.close();
		db.close();
		return sites;
	}

}
