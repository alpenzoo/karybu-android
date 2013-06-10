package com.arnia.karybu;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuTextyle;
import com.arnia.karybu.data.KarybuDatabaseHelper;
import com.arnia.karybu.data.KarybuSite;
import com.arnia.karybu.global_settings.GlobalSettingsController;
import com.arnia.karybu.members.MembersController;
import com.arnia.karybu.menus.MenuController;
import com.arnia.karybu.pages.AddPageController;
import com.arnia.karybu.pages.PageController;
import com.arnia.karybu.textyle.comments.TextyleCommentsController;
import com.arnia.karybu.textyle.posts.TextyleAddPostController;
import com.arnia.karybu.textyle.posts.TextylePostsController;

public class DashboardController extends KarybuFragment implements
		OnClickListener {

	private Spinner selectSiteSpinner;
	private ArrayList<KarybuSite> sites;
	private ArrayList<Object> sitesAndVirtualSites;
	private SiteAdapter siteAdapter;
	private KarybuSite selectingSite;
	private View view;

	private TextView newPost;
	private TextView managePosts;
	private TextView newPage;
	private TextView managePages;
	private TextView manageMenus;

	private TextView commentCount;
	private TextView quickSetting;
	private TextView userSetting;
	private TextView comment;
	private TextView manageSite;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		sitesAndVirtualSites = new ArrayList<Object>();
		super.onCreate(savedInstanceState);

		KarybuFragment
				.startProgress(getActivity(), getString(R.string.loading));
		KarybuDatabaseHelper dbHelper = KarybuDatabaseHelper
				.getDBHelper(this.activity);
		sitesAndVirtualSites.clear();
		sites = dbHelper.getAllSites();

		for (int i = 0; i < sites.size(); i++) {
			LogInInBackground loginTask = new LogInInBackground() {
				@Override
				protected void onPostExecute(KarybuSite result) {
					dismissProgress();
					sitesAndVirtualSites.add(site);
					if (array != null && array.textyles != null) {
						sitesAndVirtualSites.addAll(array.textyles);
						siteAdapter.notifyDataSetChanged();
					} else {
						Toast.makeText(activity, R.string.no_textyle,
								Toast.LENGTH_LONG).show();
					}
				}
			};
			loginTask.execute(sites.get(i));

		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.view = inflater.inflate(R.layout.layout_dashboard, container,
				false);

		StatisticsController statisticController = new StatisticsController();

		addNestedFragment(R.id.DASHBOARD_FRAGMENT_HOLDER, statisticController,
				"StatisticController");

		newPost = (TextView) this.view.findViewById(R.id.DASHBOARD_NEW_POST);
		newPost.setOnClickListener(this);

		managePosts = (TextView) view.findViewById(R.id.DASHBOARD_MANAGE_POSTS);
		managePosts.setOnClickListener(this);

		newPage = (TextView) this.view.findViewById(R.id.DASHBOARD_NEW_PAGE);
		newPage.setOnClickListener(this);

		managePages = (TextView) this.view
				.findViewById(R.id.DASHBOARD_MANAGE_PAGES);
		managePages.setOnClickListener(this);

		quickSetting = (TextView) this.view
				.findViewById(R.id.DASHBOARD_QUICK_SETTINGS);
		quickSetting.setOnClickListener(this);

		comment = (TextView) this.view.findViewById(R.id.DASHBOARD_COMMENTS);
		comment.setOnClickListener(this);

		userSetting = (TextView) this.view.findViewById(R.id.DASHBOARD_USERS);
		userSetting.setOnClickListener(this);

		manageSite = (TextView) this.view
				.findViewById(R.id.DASHBOARD_MANAGE_WEBSITE);
		manageSite.setOnClickListener(this);

		manageMenus = (TextView) this.view
				.findViewById(R.id.DASHBOARD_MENU_MANAGER);
		manageMenus.setOnClickListener(this);

		selectSiteSpinner = (Spinner) actionBar.getCustomView().findViewById(
				R.id.MENU_SELECT_SITE);

		siteAdapter = new SiteAdapter(sitesAndVirtualSites);
		selectSiteSpinner.setAdapter(siteAdapter);

		commentCount = (TextView) view
				.findViewById(R.id.DASHBOARD_COMMENT_COUNT);

		// Handle login when user change site
		selectSiteSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Object selectedItem = parent
								.getItemAtPosition(position);
						if (selectedItem.getClass() == KarybuSite.class) {
							KarybuFragment.startProgress(getActivity(),
									getString(R.string.logging));
							selectingSite = (KarybuSite) selectedItem;
							((MainActivityController) activity)
									.setSelectingSite(selectingSite);
							new LogInInBackground()
									.execute((KarybuSite) selectedItem);
						} else {
							MainActivityController xeActivity = (MainActivityController) activity;
							Fragment currentDisplayFragment = xeActivity
									.getCurrentDisplayedFragment();
							if (currentDisplayFragment.getClass() == TextylePostsController.class) {
								TextylePostsController currentPostController = (TextylePostsController) currentDisplayFragment;
								currentPostController
										.setSelectedTextyle((KarybuTextyle) selectedItem);
								currentPostController.refreshContent();
							} else if (currentDisplayFragment.getClass() == TextyleCommentsController.class) {
								TextyleCommentsController currentPostController = (TextyleCommentsController) currentDisplayFragment;
								currentPostController
										.setTextyle((KarybuTextyle) selectedItem);
							}

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});

		return this.view;
	}

	@Override
	public void onClick(View v) {
		MainActivityController mainActivity = (MainActivityController) this.activity;

		switch (v.getId()) {
		case R.id.DASHBOARD_NEW_POST:
			mainActivity.addMoreScreen(new TextyleAddPostController());
			break;

		case R.id.DASHBOARD_MANAGE_POSTS:
			TextylePostsController textylePostController = new TextylePostsController();
			KarybuTextyle textyle = (KarybuTextyle) sitesAndVirtualSites
					.get(sites.indexOf(selectingSite) + 1);
			textylePostController.setSelectedTextyle(textyle);
			mainActivity.addMoreScreen(textylePostController);
			break;
		case R.id.DASHBOARD_NEW_PAGE:
			mainActivity.addMoreScreen(new AddPageController());
			break;

		case R.id.DASHBOARD_MANAGE_PAGES:
			mainActivity.addMoreScreen(new PageController());
			break;
		case R.id.DASHBOARD_MENU_MANAGER:
			mainActivity.addMoreScreen(new MenuController());
			break;
		case R.id.DASHBOARD_QUICK_SETTINGS:
			mainActivity.addMoreScreen(new GlobalSettingsController());
			break;
		case R.id.DASHBOARD_USERS:
			mainActivity.addMoreScreen(new MembersController());
			break;
		case R.id.DASHBOARD_COMMENTS:
			KarybuTextyle textyle2 = (KarybuTextyle) sitesAndVirtualSites
					.get(sites.indexOf(selectingSite) + 1);
			TextyleCommentsController textyleCommentController = new TextyleCommentsController();
			textyleCommentController.setTextyle(textyle2);
			mainActivity.addMoreScreen(textyleCommentController);
			break;
		case R.id.DASHBOARD_MANAGE_WEBSITE:
			mainActivity.requestToBrowser();
			break;
		}
	}

	// AsyncTask for LogIn
	private class LogInInBackground extends
			AsyncTask<KarybuSite, Void, KarybuSite> {

		protected KarybuArrayList array;
		protected KarybuSite site;

		// send the request in background

		@SuppressWarnings("finally")
		@Override
		protected synchronized KarybuSite doInBackground(KarybuSite... params) {

			site = params[0];

			try {

				// set address in KarybuHost singleton
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
				try {
					array = serializer.read(KarybuArrayList.class, reader,
							false);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				return site;
			}
		}

		// verify the response after the request received a response
		@Override
		protected void onPostExecute(KarybuSite result) {
			super.onPostExecute(result);
			int count = 0;
			for (int i = 0; i < array.textyles.size(); i++) {
				count += Integer.parseInt(array.textyles.get(i).comment_count);
			}
			commentCount.setText(count + " NEW");

			dismissProgress();
		}
	}

	public class SiteAdapter extends BaseAdapter {
		private ArrayList<Object> data;

		public SiteAdapter(ArrayList<Object> data) {
			this.setData(data);
		}

		public void setData(ArrayList<Object> data) {
			this.data = data;
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
				LayoutInflater layoutInflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(
						R.layout.layout_site_spinner_item, null);
			}
			TextView textRow = (TextView) convertView
					.findViewById(R.id.SITE_SPINNER_ITEM);
			textRow.setTag(position);
			Object obj = sitesAndVirtualSites.get(position);
			if (obj.getClass() == KarybuTextyle.class) {
				textRow.setText(((KarybuTextyle) obj).textyle_title);
			} else {
				textRow.setText(obj.toString());
			}
			return convertView;
		}

	}

}
