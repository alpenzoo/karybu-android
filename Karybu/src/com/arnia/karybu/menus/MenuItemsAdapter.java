package com.arnia.karybu.menus;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.arnia.karybu.R;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuMenuItem;
import com.arnia.karybu.classes.KarybuResponse;

//Adapter for MenuItems list
public class MenuItemsAdapter extends BaseAdapter implements
		OnClickListener {
	private Context context;
	private ArrayList<KarybuMenuItem> arrayWithMenuItems;
	private ArrayList<KarybuMenuItem> arrayWholeMenuItems;
	private String menuItemParentSRL;
	private String menuSRL;

	// setter
	public void setArrayWithMenuItems(ArrayList<KarybuMenuItem> arrayWithMenuItems) {
		this.arrayWithMenuItems = arrayWithMenuItems;
	}

	public void setWholeMenuItemsOfAMenu(
			ArrayList<KarybuMenuItem> arrayWholeMenuItems) {
		this.arrayWholeMenuItems = arrayWholeMenuItems;
	}

	// constructor
	public MenuItemsAdapter(Context context, String menuitemParentSRL,
			String menuSRL) {
		this.context = context;
		this.arrayWithMenuItems = new ArrayList<KarybuMenuItem>();
		this.menuItemParentSRL = menuitemParentSRL;
		this.menuSRL = menuSRL;
	}

	@Override
	public int getCount() {
		if (arrayWithMenuItems == null)
			return 0;
		return this.arrayWithMenuItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.arrayWithMenuItems.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		KarybuMenuItem menuItem = this.arrayWithMenuItems.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.cellview_edit_menu_item, null);
		}

		TextView menuItemNameTextView = (TextView) convertView
				.findViewById(R.id.MENUITEMEDIT_TITLE_TEXT);
		menuItemNameTextView.setText(menuItem.menuItemName);

		Button editButton = (Button) convertView
				.findViewById(R.id.MENUITEMEDIT_EDITBUTTON);
		editButton.setTag(position);
		editButton.setOnClickListener(this);

		Button deleteButton = (Button) convertView
				.findViewById(R.id.MENUITEMEDIT_DELETEBUTTON);
		deleteButton.setTag(position);
		deleteButton.setOnClickListener(this);

		Button upButton = (Button) convertView
				.findViewById(R.id.MENUITEMEDIT_UPBUTTON);
		upButton.setOnClickListener(this);
		upButton.setTag(position);

		Button downButton = (Button) convertView
				.findViewById(R.id.MENUITEMEDIT_DOWNBUTTON);
		downButton.setOnClickListener(this);
		downButton.setTag(position);

		Button submenuButton = (Button) convertView
				.findViewById(R.id.MENUITEMEDIT_SUBMENUBUTTON);
		submenuButton.setOnClickListener(this);
		submenuButton.setTag(position);

		return convertView;
	}

	// method called when one of the buttons is pressed: Edit Button or Delete
	// Button
	@Override
	public void onClick(View v) {
		final int index = (Integer) v.getTag();
		final KarybuMenuItem menuItem = arrayWithMenuItems.get(index);

		if (v.getId() == R.id.MENUITEMEDIT_EDITBUTTON) {
			// change invoke activity to fragment

			Bundle args = new Bundle();
			args.putString("menu_parent_srl", menuItemParentSRL);
			args.putString("menu_item_srl", menuItem.srl);
			args.putString("menu_srl", menuSRL);

			MenuItemEditController menuEditItem = new MenuItemEditController();
			menuEditItem.setArguments(args);
			MainActivityController mainActivity = (MainActivityController) context;
			mainActivity.addMoreScreen(menuEditItem);

		} else if (v.getId() == R.id.MENUITEMEDIT_DELETEBUTTON) {
			AlertDialog confirmDelete = new AlertDialog.Builder(context)
					.setTitle(R.string.delete_menu_item_dialog_title)
					.setMessage(R.string.delete_menu_item_dialog_description)
					.setNegativeButton(
							R.string.delete_menu_item_dialog_no,
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}

							})
					.setPositiveButton(
							R.string.delete_menu_item_dialog_yes,
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									DeleteMenuItemAsyncTask task = new DeleteMenuItemAsyncTask();
									task.execute(new String[] { menuItem.srl,
											Integer.toString(index) });
								}

							})

					.create();
			confirmDelete.show();

		} else if (v.getId() == R.id.MENUITEMEDIT_UPBUTTON) {
			moveCurrentItem((Integer) v.getTag(), -1);

		} else if (v.getId() == R.id.MENUITEMEDIT_DOWNBUTTON) {
			moveCurrentItem((Integer) v.getTag(), 1);
		} else if (v.getId() == R.id.MENUITEMEDIT_SUBMENUBUTTON) {
			MainActivityController mainActivity = (MainActivityController) context;
			MenuItemsController submenuController = new MenuItemsController();
			Bundle args = new Bundle();
			args.putString("menu_srl", menuSRL);
			args.putString("menu_item_parent_srl", menuItem.srl);
			submenuController.setArguments(args);
			mainActivity.addMoreScreen(submenuController);
		}
	}

	private void moveCurrentItem(int itemPosition, int increment) {
		KarybuMenuItem item = this.arrayWithMenuItems.get(itemPosition);
		if (increment < 0) {
			if (itemPosition + increment >= 0) {
				this.arrayWithMenuItems.add(itemPosition + increment, item);
				this.arrayWithMenuItems.remove(itemPosition + 1);
				notifyDataSetChanged();
				OrderMenuItemAsyncTask orderTask = new OrderMenuItemAsyncTask();
				orderTask.execute();
			}
		} else if (increment > 0) {
			if (itemPosition + increment + 1 <= this.arrayWithMenuItems.size()) {
				this.arrayWithMenuItems.add(itemPosition + increment + 1, item);
				this.arrayWithMenuItems.remove(itemPosition);
				notifyDataSetChanged();
				OrderMenuItemAsyncTask orderTask = new OrderMenuItemAsyncTask();
				orderTask.execute();
			}
		} else {
			return;
		}
	}

	// Async Task that order a menu item
	private class OrderMenuItemAsyncTask extends
			AsyncTask<Object, Object, Object> {
		String response;

		@Override
		protected Object doInBackground(Object... param) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationArrangeMenu");
			params.put("menu_srl", menuSRL);
			params.put("title", "welcome_menu");
			String getParam = "?";

			getParam += requestedParamForSavingMenuOrder(arrayWholeMenuItems,
					"0");

			response = KarybuHost.getINSTANCE().postMultipart(params,
					"/index.php" + getParam);
			return null;
		}

		private String requestedParamForSavingMenuOrder(
				ArrayList<KarybuMenuItem> wholeMenuItems, String parentSRL) {
			String result = "";
			KarybuMenuItem indexMenuItem = null;
			for (int i = 0; i < wholeMenuItems.size(); i++) {
				indexMenuItem = wholeMenuItems.get(i);
				result += "item_key[]=" + indexMenuItem.srl + "&";
				result += "item_layout_key[]=" + indexMenuItem.srl + "&";
				result += "parent_key[]=" + parentSRL + "&";
				if (indexMenuItem.menuItems != null) {
					result += requestedParamForSavingMenuOrder(
							indexMenuItem.menuItems, indexMenuItem.srl);
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				KarybuResponse confirmation = serializer.read(KarybuResponse.class,
						reader, false);

				if (confirmation.value.equals("true")) {
					Toast.makeText(context, "Update success", Toast.LENGTH_LONG)
							.show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Async Task that deletes a menu item
	private class DeleteMenuItemAsyncTask extends
			AsyncTask<String, Object, Object> {
		String response;
		String menu_item_srl;
		int index;

		@Override
		protected Object doInBackground(String... param) {
			menu_item_srl = param[0];
			index = Integer.parseInt(param[1]);

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationDeleteMenuItem");
			// params.put("menu_srl", menuItemParentSRL);
			params.put("menu_srl", menuSRL);
			params.put("menu_item_srl", menu_item_srl);

			response = KarybuHost.getINSTANCE().postMultipart(params, "/index.php");
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				KarybuResponse confirmation = serializer.read(KarybuResponse.class,
						reader, false);

				if (confirmation.value.equals("true")) {
					arrayWithMenuItems.remove(index);
					notifyDataSetChanged();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
