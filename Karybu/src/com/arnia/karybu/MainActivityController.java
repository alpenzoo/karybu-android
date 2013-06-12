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
import android.support.v4.app.Fragment;
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
import com.arnia.karybu.textyle.comments.TextyleCommentsController;
import com.arnia.karybu.textyle.posts.TextylePostsController;

public class MainActivityController extends FragmentActivity implements
		OnPageChangeListener, OnItemSelectedListener {

	private ViewPager pager;
	private PageAdapter pageAdapter;
	private int prevPageIndex;
	private ActionBar actionBar;

	private Spinner selectSiteSpinner;
	private ArrayList<KarybuSite> sites;
	private SiteAdapter siteAdapter;
	private KarybuSite selectingSite;

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
		return selectingSite;
	}

	public KarybuTextyle getSelectedTextyle() {
		if (selectSiteSpinner.getSelectedItem().getClass() == KarybuSite.class) {
			int textyleIndex = selectSiteSpinner.getSelectedItemPosition() + 1;
			if (textyleIndex >= siteAdapter.getCount())
				return new KarybuTextyle();
			else {
				KarybuTextyle textyle = (KarybuTextyle) siteAdapter
						.getItem(textyleIndex);
				return textyle;
			}
		} else {
			KarybuTextyle textyle = (KarybuTextyle) selectSiteSpinner
					.getSelectedItem();
			return textyle;
		}
	}

	public void requestToBrowser() {
		if (selectingSite != null) {
			Intent browser = new Intent(Intent.ACTION_VIEW,
					Uri.parse(selectingSite.siteUrl));
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

	public Fragment getCurrentDisplayedFragment() {
		return this.pageAdapter.getItem(this.pager.getCurrentItem());
	}

	public void addMoreScreen(Fragment screen) {
		pageAdapter.addFragment(screen);
		pager.setCurrentItem(pageAdapter.getCount() - 1, true);
	}

	public void backwardScreen() {
		// Fragment oldScreen = pageAdapter.getItem(pageAdapter.getCount()-1);
		pager.setCurrentItem(pageAdapter.getCount() - 2, true);
	}

	private class PageAdapter extends FragmentStatePagerAdapter {

		ArrayList<Fragment> screenStack;

		public PageAdapter(FragmentManager fm) {
			super(fm);
			screenStack = new ArrayList<Fragment>();
		}

		@Override
		public Fragment getItem(int position) {
			return screenStack.get(position);

		}

		public void addFragment(Fragment screen) {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(MainActivityController.this,
					getString(R.string.logging));
		}

		// send the request in background

		@Override
		protected synchronized Boolean doInBackground(KarybuSite... params) {
			KarybuSite site = params[0];
			String url = site.siteUrl;
			String userid = site.userName;
			String password = site.password;
			try {
				KarybuHost.getINSTANCE().setURL(url);
				KarybuHost
						.getINSTANCE()
						.getRequest(
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
				try {
					String url = site.siteUrl;
					String userid = site.userName;
					String password = site.password;

					KarybuHost.getINSTANCE().setURL(url);

					KarybuHost
							.getINSTANCE()
							.getRequest(
									"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
											+ userid + "&password=" + password);

					String response = KarybuHost
							.getINSTANCE()
							.getRequest(
									"/index.php?module=mobile_communication&act=procmobile_communicationTextyleList");

					// parsing the response
					Serializer serializer = new Persister();
					Reader reader = new StringReader(response);
					KarybuArrayList array = serializer.read(
							KarybuArrayList.class, reader, false);
					sitesAndTextyles.add(site);
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
			selectingSite = (KarybuSite) selectedItem;
			new LogInInBackground().execute((KarybuSite) selectedItem);
		} else {
			Fragment currentDisplayFragment = getCurrentDisplayedFragment();
			if (currentDisplayFragment.getClass() == TextylePostsController.class) {
				TextylePostsController currentPostController = (TextylePostsController) currentDisplayFragment;
				currentPostController.onTextyleChange();
			} else if (currentDisplayFragment.getClass() == TextyleCommentsController.class) {
				TextyleCommentsController currentPostController = (TextyleCommentsController) currentDisplayFragment;
				currentPostController.onTextyleChange();
			}

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
