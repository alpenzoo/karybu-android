package com.arnia.karybu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.view = inflater.inflate(R.layout.layout_dashboard, container,
				false);

		commentCount = (TextView) view
				.findViewById(R.id.DASHBOARD_COMMENT_COUNT);

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
}
