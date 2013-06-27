package com.arnia.karybu.menus;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuMenu;
import com.arnia.karybu.controls.KarybuDialog;

//Adapter for Menu list
public class MenuAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	private ArrayList<KarybuMenu> arrayWithMenus;

	// getter
	public ArrayList<KarybuMenu> getArrayWithMenus() {
		return arrayWithMenus;
	}

	// setter
	public void setArrayWithMenus(ArrayList<KarybuMenu> arrayWithMenus) {
		this.arrayWithMenus = arrayWithMenus;
	}

	// constructor
	public MenuAdapter(Context context) {

		this.arrayWithMenus = new ArrayList<KarybuMenu>();
		this.context = context;
	}

	@Override
	public int getCount() {
		if (arrayWithMenus == null)
			return 0;
		return arrayWithMenus.size();
	}

	@Override
	public Object getItem(int position) {
		return this.arrayWithMenus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		KarybuMenu menu = this.arrayWithMenus.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.cellview_menu, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.MENUCELL_TEXTVIEW);
		textView.setText(menu.menuName);

		Button deleteButton = (Button) convertView
				.findViewById(R.id.MENUCELL_DELETEBUTTON);

		deleteButton.setOnClickListener(this);
		deleteButton.setTag(position);

		return convertView;
	}

	@Override
	public void onClick(View v) {
		final int index = (Integer) v.getTag();
		final KarybuMenu menu = this.arrayWithMenus.get(index);
		switch (v.getId()) {
		case R.id.MENUCELL_DELETEBUTTON:
			final KarybuDialog dialog = new KarybuDialog(context);
			dialog.setTitle(R.string.delete_menu_item_dialog_title);
			dialog.setMessage(R.string.delete_menu_item_dialog_description);
			dialog.setPositiveButton(R.string.yes, new OnClickListener() {

				@Override
				public void onClick(View v) {
					DeleteMenuAsyncTask task = new DeleteMenuAsyncTask();
					task.execute(menu);
					dialog.dismiss();
				}
			});
			dialog.setNegativeButton(R.string.no);
			dialog.show();
			break;
		}
	}

	// async task that delete a menu
	private class DeleteMenuAsyncTask extends AsyncTask<KarybuMenu, Void, Void> {

		private KarybuMenu menuToDelete;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(context,
					context.getString(R.string.processing));
		}

		@Override
		protected Void doInBackground(KarybuMenu... param) {
			menuToDelete = param[0];
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("menu_srl", menuToDelete.menuSrl);
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationMenuDelete");
			KarybuHost.getINSTANCE().postMultipart(params, "/");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
			arrayWithMenus.remove(menuToDelete);
			notifyDataSetChanged();
		}
	}

}
