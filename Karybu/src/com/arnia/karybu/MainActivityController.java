package com.arnia.karybu;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuTextyle;
import com.arnia.karybu.data.KarybuDatabaseHelper;
import com.arnia.karybu.data.KarybuSite;
import com.arnia.karybu.sites.SiteController;

public class MainActivityController extends FragmentActivity implements
		OnPageChangeListener, OnItemSelectedListener {

	private ViewPager pager;
	private PageAdapter pageAdapter;
	private int prevPageIndex;
	private ActionBar actionBar;

	private Spinner selectSiteSpinner;
	private ArrayList<KarybuSite> sites;
	private SiteAdapter siteAdapter;
	private KarybuSite selectedSite;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main_activity);

		actionBar = getActionBar();
		actionBar.setCustomView(R.layout.bar_action);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_action_bar));

		pager = (ViewPager) findViewById(R.id.pager);
		pageAdapter = new PageAdapter(getSupportFragmentManager());

		pager.setAdapter(pageAdapter);
		pageAdapter.addFragment(new DashboardController());
		pager.setOnPageChangeListener(this);

		KarybuDatabaseHelper dbHelper = KarybuDatabaseHelper.getDBHelper(this);
		sites = dbHelper.getAllSites();

		LoadAllTextylesInBackground task = new LoadAllTextylesInBackground();
		task.execute(sites);

		selectSiteSpinner = (Spinner) actionBar.getCustomView().findViewById(
				R.id.MENU_SELECT_SITE);

		siteAdapter = new SiteAdapter();
		selectSiteSpinner.setAdapter(siteAdapter);
		selectSiteSpinner.setOnItemSelectedListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	public KarybuSite getSelectedSite() {
		return selectedSite;
	}

	public KarybuTextyle getSelectedTextyle() {
		if (selectSiteSpinner.getSelectedItem().getClass() == KarybuSite.class) {
			int textyleIndex = selectSiteSpinner.getSelectedItemPosition() + 1;
			if (textyleIndex >= siteAdapter.getCount())
				return null;
			else {
				Object obj = siteAdapter.getItem(textyleIndex);
				if (obj.getClass() == KarybuTextyle.class)
					return (KarybuTextyle) obj;
				else
					return null;
			}
		} else {
			KarybuTextyle textyle = (KarybuTextyle) selectSiteSpinner
					.getSelectedItem();
			return textyle;
		}
	}

	public void requestToBrowser() {
		if (selectedSite != null) {
			Intent browser = new Intent(Intent.ACTION_VIEW,
					Uri.parse(selectedSite.siteUrl));
			this.startActivity(browser);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings_website_manager:
			addMoreScreen(new SiteController());
			break;
		case R.id.menu_settings_help:
			addMoreScreen(new HelpController());
			break;
		case R.id.menu_settings_about:
			addMoreScreen(new AboutController());
			break;
		case android.R.id.home:
			backwardScreen();
			break;
		default:
			break;
		}

		return true;
	}

	public KarybuFragment getCurrentDisplayedFragment() {
		return this.pageAdapter.getItem(this.pager.getCurrentItem());
	}

	public void addMoreScreen(KarybuFragment screen) {
		pageAdapter.addFragment(screen);
		pager.setCurrentItem(pageAdapter.getCount() - 1, true);
	}

	public void backwardScreen() {
		// Fragment oldScreen = pageAdapter.getItem(pageAdapter.getCount()-1);
		pager.setCurrentItem(pageAdapter.getCount() - 2, true);
	}

	private class PageAdapter extends FragmentStatePagerAdapter {

		ArrayList<KarybuFragment> screenStack;

		public PageAdapter(FragmentManager fm) {
			super(fm);
			screenStack = new ArrayList<KarybuFragment>();
		}

		@Override
		public KarybuFragment getItem(int position) {
			return screenStack.get(position);

		}

		public void addFragment(KarybuFragment screen) {
			screenStack.add(screen);
			this.notifyDataSetChanged();

		}

		public void removeLastFragment() {
			// removeFragment(screenStack.get(getCount()-1));
			screenStack.remove(prevPageIndex);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return screenStack.size();
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int pageIndex) {
		if (prevPageIndex > pageIndex) {
			this.pageAdapter.removeLastFragment();
		}
		prevPageIndex = pageIndex;
		boolean displayHomeAsUp = pageIndex == 0 ? false : true;
		getActionBar().setDisplayHomeAsUpEnabled(displayHomeAsUp);
	}

	@Override
	public void onBackPressed() {
		if (pager.getChildCount() > 1) {
			backwardScreen();
		} else {
			super.onBackPressed();
		}
	}

	public class SiteAdapter extends BaseAdapter {
		private ArrayList<Object> data;

		public SiteAdapter() {
			data = new ArrayList<Object>();
		}

		public void setData(ArrayList<Object> data) {
			this.data = data;
		}

		public ArrayList<Object> getData() {
			return data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public int getPositionOfItem(Object item) {
			return data.indexOf(item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(
						R.layout.layout_site_spinner_item, null);
			}
			TextView textRow = (TextView) convertView
					.findViewById(R.id.SITE_SPINNER_ITEM);
			textRow.setTag(position);
			Object obj = data.get(position);
			if (obj.getClass() == KarybuTextyle.class) {
				textRow.setText(((KarybuTextyle) obj).textyle_title);
			} else {
				textRow.setText(obj.toString());
			}
			return convertView;
		}
	}

	// AsyncTask for LogIn
	private class LogInInBackground extends
			AsyncTask<KarybuSite, Void, Boolean> {

		protected KarybuSite site;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(MainActivityController.this,
					getString(R.string.logging));
		}

		// send the request in background

		@Override
		protected synchronized Boolean doInBackground(KarybuSite... params) {
			site = params[0];
			String url = site.siteUrl;
			String userid = site.userName;
			String password = site.password;
			try {
				KarybuHost.getINSTANCE().setURL(url);
				KarybuHost
						.getINSTANCE()
						.postRequest(
								"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ userid + "&password=" + password);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
		}
	}

	private class LoadAllTextylesInBackground extends
			AsyncTask<ArrayList<KarybuSite>, Void, ArrayList<Object>> {

		private ArrayList<Object> sitesAndTextyles;
		private ArrayList<KarybuSite> sites;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(MainActivityController.this,
					getString(R.string.loading));
		}

		@Override
		protected synchronized ArrayList<Object> doInBackground(
				ArrayList<KarybuSite>... params) {
			sites = params[0];
			sitesAndTextyles = new ArrayList<Object>();
			for (KarybuSite site : sites) {
				sitesAndTextyles.add(site);
				try {
					String url = site.siteUrl;
					String userid = site.userName;
					String password = site.password;

					KarybuHost.getINSTANCE().setURL(url);

					KarybuHost
							.getINSTANCE()
							.postRequest(
									"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
											+ userid + "&password=" + password);

					String response = KarybuHost
							.getINSTANCE()
							.postRequest(
									"/index.php?module=mobile_communication&act=procmobile_communicationTextyleList");

					// parsing the response
					Serializer serializer = new Persister();
					Reader reader = new StringReader(response);
					KarybuArrayList array = serializer.read(
							KarybuArrayList.class, reader, false);
					if (array != null && array.textyles != null)
						sitesAndTextyles.addAll(array.textyles);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return sitesAndTextyles;
		}

		@Override
		protected void onPostExecute(ArrayList<Object> result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
			siteAdapter.setData(result);
			siteAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		Object selectedItem = parent.getItemAtPosition(position);
		if (selectedItem.getClass() == KarybuSite.class) {
			KarybuSite site = (KarybuSite) selectedItem;
			if (site != selectedSite) {
				// Log.i("TAG","[MaintActivity]Change from site to site");
				selectedSite = (KarybuSite) selectedItem;
				new LogInInBackground() {
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						KarybuFragment.dismissProgress();
						KarybuFragment currentScreen = getCurrentDisplayedFragment();
						currentScreen.onSelectedSite(site);
						KarybuTextyle textyle = getSelectedTextyle();
						currentScreen.onSelectedTextyle(textyle);
					}
				}.execute((KarybuSite) selectedItem);
			} else {
				// Log.i("TAG","[MaintActivity]Change from textyle to site");
				KarybuFragment currentScreen = getCurrentDisplayedFragment();
				KarybuTextyle textyle = getSelectedTextyle();
				currentScreen.onSelectedTextyle(textyle);
			}
		} else {
			KarybuTextyle textyle = (KarybuTextyle) selectedItem;
			KarybuSite site = getSiteByTextyle(textyle, position);
			if (site != selectedSite) {
				selectedSite = site;
				// Log.i("TAG","[MaintActivity]Change from textyle to site of another site");
				new LogInInBackground() {
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						KarybuFragment.dismissProgress();
						KarybuFragment currentScreen = getCurrentDisplayedFragment();
						KarybuTextyle textyle = getSelectedTextyle();
						currentScreen.onSelectedTextyle(textyle);
					}
				}.execute(selectedSite);
			} else {
				// Log.i("TAG","[MaintActivity]Change from textyle to textyle in current site");
				KarybuFragment currentScreen = getCurrentDisplayedFragment();
				currentScreen.onSelectedTextyle(textyle);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private KarybuSite getSiteByTextyle(KarybuTextyle textyle, int itemIndex) {
		ArrayList<Object> sitesAndTextyles = siteAdapter.getData();
		for (int i = (itemIndex - 1); i >= 0; i--) {
			Object item = sitesAndTextyles.get(i);
			if (item.getClass() == KarybuSite.class) {
				return (KarybuSite) item;
			}
		}
		return null;
	}

}
