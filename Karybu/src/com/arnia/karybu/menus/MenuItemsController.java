package com.arnia.karybu.menus;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.arnia.karybu.R;
import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuMenu;
import com.arnia.karybu.classes.KarybuMenuItem;

public class MenuItemsController extends KarybuFragment {
	private String menuItemParentSRL;
	private String menuSRL;
	private ArrayList<KarybuMenuItem> arrayWithMenuItems;
	private MenuItemsAdapter adapter;

	private Button addMenuItemButton;
	boolean isOnPause;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_edit_menu,
				container, false);

		ListView listView = (ListView) view
				.findViewById(R.id.EDITMENU_LISTVIEW);
		addMenuItemButton = (Button) view
				.findViewById(R.id.EDITMENU_ADDBUTTON);

		addMenuItemButton.setOnClickListener(new OnClickListener() {
			// method called when the Add Button is pressed
			@Override
			public void onClick(View v) {
				MainActivityController mainActivity = (MainActivityController) activity;
				KarybuFragment screen = new AddMenuItemController();
				Bundle args = new Bundle();
				args.putString("menu_srl", menuSRL);
				args.putString("menu_parent_srl", menuItemParentSRL);
				screen.setArguments(args);
				mainActivity.addMoreScreen(screen);
			}
		});

		Bundle argument = getArguments();
		menuSRL = argument.getString("menu_srl");
		menuItemParentSRL = argument.getString("menu_item_parent_srl");
		adapter = new MenuItemsAdapter(activity, menuItemParentSRL,
				menuSRL);
		listView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		KarybuFragment.startProgress(getActivity(), "Logging...");
		GetMenusAsyncTask getAsyncTask = new GetMenusAsyncTask();
		getAsyncTask.execute();
	}

	@Override
	public void onPause() {
		super.onPause();
		isOnPause = true;
	}

	// Async Task request to get a list of MenuItems
	private class GetMenusAsyncTask extends AsyncTask<Object, Object, Object> {
		KarybuArrayList arrayWithMenus = null;
		String xmlData;

		@Override
		protected Object doInBackground(Object... params) {
			// send request
			xmlData = KarybuHost
					.getINSTANCE()
					.getRequest(
							"/index.php?XDEBUG_SESSION_START=netbeans-xdebug&module=mobile_communication&act=procmobile_communicationDisplayMenu");

			// parse response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(xmlData);
			try {
				arrayWithMenus = serializer.read(KarybuArrayList.class, reader,
						false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			// isLoggedIn(xmlData, MenuItemsController.this);

			dismissProgress();
			int i;
			if (arrayWithMenus != null && arrayWithMenus.menus != null) {
				// Get menu array of menu by parentSRL
				for (i = 0; i < arrayWithMenus.menus.size(); i++) {
					KarybuMenu menu = arrayWithMenus.menus.get(i);
					if (menu.menuSrl.compareTo(menuSRL) == 0
							&& menuItemParentSRL.compareTo("0") == 0) {
						arrayWithMenuItems = menu.menuItems;
						adapter.setWholeMenuItemsOfAMenu(arrayWithMenuItems);
						break;
					}
					if (menu.menuItems != null) {
						arrayWithMenuItems = getSubMenuOfMenuParent(
								menu.menuItems, menuItemParentSRL);
						if (arrayWithMenuItems != null) {
							adapter.setWholeMenuItemsOfAMenu(menu.menuItems);
							break;

						}
					}
				}

				adapter.setArrayWithMenuItems(arrayWithMenuItems);
				adapter.notifyDataSetChanged();
			}
		}

		private ArrayList<KarybuMenuItem> getSubMenuOfMenuParent(
				ArrayList<KarybuMenuItem> menuItemlist, String menuItemParentSRL) {
			// Get menu array of menu by parentSRL
			ArrayList<KarybuMenuItem> result = null;
			if (menuItemlist != null) {
				for (KarybuMenuItem menuItem : menuItemlist) {
					if (menuItem.srl.compareTo(menuItemParentSRL) == 0) {
						return menuItem.menuItems;
					} else {
						result = getSubMenuOfMenuParent(menuItem.menuItems,
								menuItemParentSRL);
						if (result != null) {
							return result;
						}
					}
				}
			}
			return result;
		}

	}
}
