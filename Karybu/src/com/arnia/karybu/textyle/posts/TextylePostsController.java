package com.arnia.karybu.textyle.posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuPagination;
import com.arnia.karybu.classes.KarybuTextyle;
import com.arnia.karybu.classes.KarybuTextylePost;
import com.arnia.karybu.controls.SegmentedRadioGroup;

public class TextylePostsController extends KarybuFragment implements
		OnClickListener, OnItemClickListener, OnScrollListener,
		OnCheckedChangeListener {

	// UI references
	private ListView listView;
	private View listViewFotter;
	private TextylePostAdapter adapter;
	private SegmentedRadioGroup radioGroup;

	private KarybuTextyle textyle;

	private KarybuArrayList[] postsArray;
	private boolean[] isTaskLoading;

	private final int POST_TYPE_ALL = 0;
	private final int POST_TYPE_PUBLISHED = 1;
	private final int POST_TYPE_DRAFT = 2;
	private final int POST_TYPE_TRASH = 3;

	private View fragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		textyle = ((MainActivityController) activity).getSelectedTextyle();

		fragmentView = inflater.inflate(R.layout.layout_textyle_posts,
				container, false);

		postsArray = new KarybuArrayList[4];
		isTaskLoading = new boolean[4];

		// UI reference
		radioGroup = (SegmentedRadioGroup) fragmentView
				.findViewById(R.id.POST_FILTER);
		radioGroup.setOnCheckedChangeListener(this);

		listView = (ListView) fragmentView
				.findViewById(R.id.TEXTYLE_POSTS_LISTVIEW);
		// Add loading bar to listview footer
		listViewFotter = ((LayoutInflater) activity
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_listview_loading_footer, null, false);
		listView.addFooterView(listViewFotter);
		listViewFotter.setVisibility(View.INVISIBLE);

		// Get virtual site and add it to action bar
		adapter = new TextylePostAdapter(activity);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnScrollListener(this);

		return fragmentView;
	}

	@Override
	protected void onSelectedTextyle(KarybuTextyle textyle) {
		super.onSelectedTextyle(textyle);
		Log.i("leapkh", "[TextylePostController]onSelectedTextyle");
		this.textyle = textyle;
		postsArray = new KarybuArrayList[4];
		isTaskLoading = new boolean[4];
		adapter.clearData();
		if (textyle != null)
			onCheckedChanged(radioGroup, radioGroup.getCheckedRadioButtonId());
	}

	private class GetPostsAsycTask extends
			AsyncTask<Integer, Void, KarybuArrayList> {

		private int postType;

		public GetPostsAsycTask(int postType) {
			this.postType = postType;
			if (listView.getFooterViewsCount() == 0)
				listView.addFooterView(listViewFotter);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listViewFotter.setVisibility(View.VISIBLE);
			isTaskLoading[postType] = true;
		}

		@Override
		protected KarybuArrayList doInBackground(Integer... params) {
			int pageNumber = params.length == 0 ? 1 : params[0];
			String requestUrl;
			switch (postType) {
			case POST_TYPE_ALL:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&published=1&page="
						+ pageNumber;
				break;

			case POST_TYPE_PUBLISHED:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&published=2&page="
						+ pageNumber;
				break;

			case POST_TYPE_DRAFT:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&published=3&page="
						+ pageNumber;
				break;
			default:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&published=0&page="
						+ pageNumber;
			}

			String response = KarybuHost.getINSTANCE().getRequest(requestUrl);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				KarybuArrayList tmpPosts = serializer.read(
						KarybuArrayList.class, reader, false);
				return tmpPosts;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(KarybuArrayList result) {
			super.onPostExecute(result);

			isTaskLoading[postType] = false;
			if (postsArray[postType] == null) {
				postsArray[postType] = new KarybuArrayList();
				postsArray[postType].posts = new ArrayList<KarybuTextylePost>();
				postsArray[postType].pagination = new KarybuPagination();
			}
			if (result != null && result.posts != null) {
				for (KarybuTextylePost post : result.posts) {
					postsArray[postType].posts.add(post);
				}
				postsArray[postType].pagination = result.pagination;
				adapter.setArrayWithPosts(postsArray[postType].posts);
				adapter.notifyDataSetChanged();
				if (postsArray[postType].pagination.cur_page == postsArray[postType].pagination.total_page)
					// listViewFotter.setVisibility(View.GONE);
					listView.removeFooterView(listViewFotter);
			}
			adapter.notifyDataSetChanged();
			listViewFotter.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT)
				.show();

		TextyleEditPostContentController editPostContentController = new TextyleEditPostContentController();

		KarybuTextylePost post = (KarybuTextylePost) parent.getAdapter()
				.getItem(position);

		Bundle args = new Bundle();
		args.putSerializable("textyle", textyle);
		args.putString("document_srl", post.document_srl);
		args.putString("title", post.title);
		args.putString("category_srl", post.category_srl);

		editPostContentController.setArguments(args);

		((MainActivityController) activity)
				.addMoreScreen(editPostContentController);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		boolean loadMore = (firstVisibleItem + visibleItemCount >= totalItemCount)
				&& totalItemCount >= 20;
		if (loadMore) {
			int postType;
			switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.TEXTYLE_POSTS_ALLOPTION:
				postType = POST_TYPE_ALL;
				break;

			case R.id.TEXTYLE_POSTS_PUBLISHEDOPTION:
				postType = POST_TYPE_PUBLISHED;
				break;

			case R.id.TEXTYLE_POSTS_DRAFTSOPTION:
				postType = POST_TYPE_DRAFT;
				break;
			case R.id.TEXTYLE_POSTS_TRASHOPTION:
				postType = POST_TYPE_TRASH;
				break;
			default:
				postType = -1;
			}

			if (postType == -1)
				return;

			if (!isTaskLoading[postType]) {
				if (postsArray[postType] != null
						&& postsArray[postType].pagination != null) {
					if (postsArray[postType].pagination.cur_page < postsArray[postType].pagination.total_page) {
						if (textyle != null) {
							GetPostsAsycTask task = new GetPostsAsycTask(
									postType);
							task.execute(postsArray[postType].pagination.cur_page + 1);
						}
					}
				}
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int postType;

		if (textyle == null)
			return;

		switch (checkedId) {
		case R.id.TEXTYLE_POSTS_ALLOPTION:
			postType = POST_TYPE_ALL;
			break;

		case R.id.TEXTYLE_POSTS_PUBLISHEDOPTION:
			postType = POST_TYPE_PUBLISHED;
			break;

		case R.id.TEXTYLE_POSTS_DRAFTSOPTION:
			postType = POST_TYPE_DRAFT;
			break;
		case R.id.TEXTYLE_POSTS_TRASHOPTION:
			postType = POST_TYPE_TRASH;
			break;
		default:
			postType = -1;
			return;
		}

		if (postType == -1)
			return;

		if (postsArray[postType] == null) {
			if (!isTaskLoading[postType]) {
				if (adapter != null)
					adapter.clearData();
				GetPostsAsycTask task = new GetPostsAsycTask(postType);
				task.execute();
			}
		} else {
			adapter.setArrayWithPosts(postsArray[postType].posts);
			adapter.notifyDataSetChanged();
		}
	}

}