package com.arnia.karybu.textyle.posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuResponse;
import com.arnia.karybu.classes.KarybuTextylePost;
import com.arnia.karybu.controls.KarybuDialog;

public class TextylePostAdapter extends BaseAdapter {
	// array with pages that appear in listview
	private ArrayList<KarybuTextylePost> arrayWithPosts;

	private Activity context;

	public void setArrayWithPosts(ArrayList<KarybuTextylePost> arrayWithPosts) {
		this.arrayWithPosts = arrayWithPosts;
	}

	public void clearData() {
		arrayWithPosts = new ArrayList<KarybuTextylePost>();
		notifyDataSetChanged();
	}

	public TextylePostAdapter(Activity context) {
		arrayWithPosts = new ArrayList<KarybuTextylePost>();
		this.context = context;
	}

	@Override
	public int getCount() {
		return arrayWithPosts.size();
	}

	@Override
	public Object getItem(int index) {
		return arrayWithPosts.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// get the page from the array
		final KarybuTextylePost post = arrayWithPosts.get(pos);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.cellview_textyle_post_item,
					null);
		}

		// construct the view's elements
		Button btnViewPost = (Button) convertView
				.findViewById(R.id.POST_VIEW_POST);
		Button btnDeletePost = (Button) convertView
				.findViewById(R.id.POST_DELETE_POST);

		TextView txtPostTitle = (TextView) convertView
				.findViewById(R.id.POST_POST_TITLE);
		txtPostTitle.setText(post.title);

		TextView txtCommentCount = (TextView) convertView
				.findViewById(R.id.POST_POST_COMMENT);
		txtCommentCount.setText(post.comment_count + " comments");

		btnViewPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri
						.parse(KarybuHost.getINSTANCE().getDomainName()
								+ post.url));
				context.startActivity(browser);
			}
		});
		btnDeletePost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final KarybuDialog dialog = new KarybuDialog(context);
				dialog.setTitle(R.string.delete_post);
				dialog.setMessage(R.string.delete_post_msg);
				dialog.setPositiveButton(R.string.yes, new OnClickListener() {

					@Override
					public void onClick(View v) {
						DeletePostAsynTask task = new DeletePostAsynTask();
						task.execute(post);
						dialog.dismiss();
					}
				});
				dialog.setNegativeButton(R.string.no);
				dialog.show();
			}
		});

		return convertView;
	}

	private class DeletePostAsynTask extends
			AsyncTask<KarybuTextylePost, Void, String> {

		private KarybuTextylePost post;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			KarybuFragment.startProgress(context,
					context.getString(R.string.processing));
		}

		@Override
		protected String doInBackground(KarybuTextylePost... params) {
			post = params[0];
			HashMap<String, String> ps = new HashMap<String, String>();
			ps.put("module", "mobile_communication");
			ps.put("act", "procmobile_communicationManageCheckedDocument");
			ps.put("type", "delete");
			ps.put("cart[]", post.document_srl);
			String strResponse = KarybuHost.getINSTANCE()
					.postMultipart(ps, "/");

			return strResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			KarybuFragment.dismissProgress();
			Serializer serializer = new Persister();
			Reader reader = new StringReader(result);
			try {
				KarybuResponse response = serializer.read(KarybuResponse.class,
						reader, false);
				if (response.error == 0) {
					arrayWithPosts.remove(post);
					notifyDataSetChanged();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Toast.makeText(context, "Error while trying to delete post.",
					Toast.LENGTH_LONG).show();
		}

	}

}
