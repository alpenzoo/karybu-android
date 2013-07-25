package com.arnia.karybu.textyle.comments;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;

//Activity that has a listView that contains KarybuComments
public class TextyleCommentsController extends KarybuFragment {
	private ListView listView;

	private KarybuArrayList array;
	private TextyleCommentsAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_textyle_comments,
				container, false);
		listView = (ListView) view.findViewById(R.id.TEXTYLE_COMMENTS_LISTVIEW);
		adapter = new TextyleCommentsAdapter(activity);
		listView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		refreshComment();
	}

	private void refreshComment() {
		GetCommentsAsyncTask task = new GetCommentsAsyncTask();
		task.execute();
	}

	// Async Task for getting the comments
	private class GetCommentsAsyncTask extends
			AsyncTask<Object, Object, Object> {
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(activity, getString(R.string.loading));
		}

		@Override
		protected Object doInBackground(Object... params) {
			// send request
			response = KarybuHost
					.getINSTANCE()
					.postRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationShowComments");

			// parse the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try {
				array = serializer.read(KarybuArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		// method called when the response came
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
			adapter.clearData();
			adapter.setComments(array.comments);
			adapter.notifyDataSetChanged();
		}
	}
}
