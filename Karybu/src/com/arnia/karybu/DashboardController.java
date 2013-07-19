package com.arnia.karybu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arnia.karybu.classes.KarybuTextyle;
import com.arnia.karybu.data.KarybuSite;
import com.arnia.karybu.members.MembersController;
import com.arnia.karybu.menus.MenuController;
import com.arnia.karybu.pages.AddPageController;
import com.arnia.karybu.pages.PageController;
import com.arnia.karybu.settings.GlobalSettingsController;
import com.arnia.karybu.textyle.comments.TextyleCommentsController;
import com.arnia.karybu.textyle.posts.TextyleAddPostController;
import com.arnia.karybu.textyle.posts.TextylePostsController;

public class DashboardController extends KarybuFragment implements
		OnClickListener {

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

	private StatisticsController statisticController;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.layout_dashboard, container, false);

		commentCount = (TextView) view
				.findViewById(R.id.DASHBOARD_COMMENT_COUNT);

		statisticController = new StatisticsController();

		addNestedFragment(R.id.DASHBOARD_FRAGMENT_HOLDER, statisticController,
				"StatisticController");

		newPost = (TextView) view.findViewById(R.id.DASHBOARD_NEW_POST);
		newPost.setOnClickListener(this);

		managePosts = (TextView) view.findViewById(R.id.DASHBOARD_MANAGE_POSTS);
		managePosts.setOnClickListener(this);

		newPage = (TextView) view.findViewById(R.id.DASHBOARD_NEW_PAGE);
		newPage.setOnClickListener(this);

		managePages = (TextView) view.findViewById(R.id.DASHBOARD_MANAGE_PAGES);
		managePages.setOnClickListener(this);

		quickSetting = (TextView) view
				.findViewById(R.id.DASHBOARD_QUICK_SETTINGS);
		quickSetting.setOnClickListener(this);

		comment = (TextView) view.findViewById(R.id.DASHBOARD_COMMENTS);
		comment.setOnClickListener(this);

		userSetting = (TextView) view.findViewById(R.id.DASHBOARD_USERS);
		userSetting.setOnClickListener(this);

		manageSite = (TextView) view
				.findViewById(R.id.DASHBOARD_MANAGE_WEBSITE);
		manageSite.setOnClickListener(this);

		manageMenus = (TextView) view.findViewById(R.id.DASHBOARD_MENU_MANAGER);
		manageMenus.setOnClickListener(this);

		return this.view;
	}

	@Override
	public void onClick(View v) {
		MainActivityController mainActivity = (MainActivityController) activity;

		switch (v.getId()) {
		case R.id.DASHBOARD_NEW_POST:
			mainActivity.addMoreScreen(new TextyleAddPostController());
			break;

		case R.id.DASHBOARD_MANAGE_POSTS:
			mainActivity.addMoreScreen(new TextylePostsController());
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
			mainActivity.addMoreScreen(new TextyleCommentsController());
			break;
		case R.id.DASHBOARD_MANAGE_WEBSITE:
			mainActivity.requestToBrowser();
			break;
		}
	}

	@Override
	protected void onSelectedSite(KarybuSite site) {
		super.onSelectedSite(site);
		if (statisticController != null)
			statisticController.refreshStatistic();
	}

	@Override
	protected void onSelectedTextyle(KarybuTextyle textyle) {
		super.onSelectedTextyle(textyle);
		String commentCountStr;
		if (textyle == null || Integer.parseInt(textyle.comment_count) == 0)
			commentCountStr = getString(R.string.no_new_comment);
		else
			commentCountStr = String.format(
					getString(R.string.new_comment_count),
					textyle.comment_count);
		commentCount.setText(commentCountStr);
	}

}
