package com.arnia.karybu;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import com.arnia.karybu.R;
import com.arnia.karybu.data.KarybuSite;
import com.arnia.karybu.sites.SiteController;

public class MainActivityController extends FragmentActivity implements
		OnPageChangeListener {

	private ViewPager pager;
	private PageAdapter pageAdapter;
	private int prevPageIndex;
	private KarybuSite selectingSite;
	private ActionBar actionBar;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	public void setSelectingSite(KarybuSite site) {
		selectingSite = site;
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
}
