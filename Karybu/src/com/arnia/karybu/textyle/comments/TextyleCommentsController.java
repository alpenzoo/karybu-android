package com.arnia.karybu.textyle.comments;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.arnia.karybu.R;
import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuComment;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuResponse;
import com.arnia.karybu.classes.KarybuTextyle;

//Activity that has a listView that contains KarybuComments
public class TextyleCommentsController extends KarybuFragment implements
		OnClickListener {
	private ListView listView;

	private KarybuTextyle textyle;

	private KarybuArrayList array;
	private TextyleCommentsAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		textyle = ((MainActivityController) activity).getSelectedTextyle();

		View view = inflater.inflate(R.layout.layout_textyle_comments,
				container, false);
		listView = (ListView) view.findViewById(R.id.TEXTYLE_COMMENTS_LISTVIEW);
		adapter = new TextyleCommentsAdapter(this);
		listView.setAdapter(adapter);

		return view;
	}

	public void onTextyleChange() {
		textyle = ((MainActivityController) activity).getSelectedTextyle();
		refreshComment();
	}

	private void refreshComment() {
		// send request to get the comments
		GetCommentsAsyncTask task = new GetCommentsAsyncTask();
		task.execute();
	}

	@Override
	public void onResume() {
		// send request to get the comments
		refreshComment();
		super.onResume();
	}

	// Async Task for getting the comments
	private class GetCommentsAsyncTask extends
			AsyncTask<Object, Object, Object> {
		String response;

		@Override
		protected Object doInBackground(Object... params) {
			// send request
			response = KarybuHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationShowComments&module_srl="
									+ textyle.module_srl);

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

			// check if the user is logged in
			if (array.comments == null) {
				Toast toast = Toast.makeText(activity, "No comments!",
						Toast.LENGTH_SHORT);
				toast.show();
			}

			adapter.setComments(array.comments);
			adapter.notifyDataSetChanged();
			// }

		}
	}

	// method called when one of the buttons is clicked: Delete Button, Reply
	// Button and Public Button
	@Override
	public void onClick(View v) {
		// get the comment
		// the view's tag is the possition in array
		KarybuComment comment = adapter.getArrayWithComments().get(
				(Integer) v.getTag());

		// cast the view as button
		Button button = (Button) v;

		// check what button was pressed
		if (v.getId() == R.id.TEXTYLE_COMMENTS_DELETE) {
			DeleteCommentAsyncTask task = new DeleteCommentAsyncTask();
			task.execute(comment);
		} else if (v.getId() == R.id.TEXTYLE_COMMENTS_REPLY) {
			TextyleCommentsReplyController commentReplyController = new TextyleCommentsReplyController();
			Bundle args = new Bundle();
			args.putSerializable("textyle", textyle);
			args.putString("document_srl", comment.document_srl);
			args.putString("comment_srl", comment.comment_srl);
			commentReplyController.setArguments(args);
			((MainActivityController) activity)
					.addMoreScreen(commentReplyController);

		} else if (v.getId() == R.id.TEXTYLE_COMMENTS_PUBLIC) {
			if (button.getText().toString().equals("Public")) {
				String requestXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
						+ "<methodCall>\n<params>\n<comment_srl><![CDATA["
						+ comment.comment_srl
						+ "]]></comment_srl>\n"
						+ "<page><![CDATA[1]]></page>\n<is_secret><![CDATA[Y]]></is_secret>\n"
						+ "<module_srl><![CDATA["
						+ comment.module_srl
						+ "]]></module_srl>\n<module><![CDATA[textyle]]></module>\n"
						+ "<act><![CDATA[procTextyleCommentItemSetSecret]]></act>\n<vid><![CDATA["
						+ textyle.domain
						+ "]]></vid>\n"
						+ "</params>\n</methodCall>";
				comment.is_secret = "Y";
				button.setText("Private");
				VisibilityChangeAsyncTask task = new VisibilityChangeAsyncTask();
				task.execute(requestXML);
			} else if (button.getText().toString().equals("Private")) {
				String requestXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
						+ "<methodCall>\n<params>\n<comment_srl><![CDATA["
						+ comment.comment_srl
						+ "]]></comment_srl>\n"
						+ "<page><![CDATA[1]]></page>\n<is_secret><![CDATA[N]]></is_secret>\n"
						+ "<module_srl><![CDATA["
						+ comment.module_srl
						+ "]]></module_srl>\n"
						+ "<module><![CDATA[textyle]]></module>\n"
						+ "<act><![CDATA[procTextyleCommentItemSetSecret]]></act>\n"
						+ "<vid><![CDATA["
						+ textyle.domain
						+ "]]></vid>\n</params>\n</methodCall>";

				comment.is_secret = "N";
				button.setText("Public");
				VisibilityChangeAsyncTask task = new VisibilityChangeAsyncTask();
				task.execute(requestXML);
			}
		}
	}

	// Async Task to remove a comment
	private class DeleteCommentAsyncTask extends
			AsyncTask<KarybuComment, String, KarybuComment> {
		KarybuResponse confirmation;

		@Override
		protected KarybuComment doInBackground(KarybuComment... params) {
			// the xml that will be sent in request
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n" + "<comment_srl><![CDATA["
					+ params[0].comment_srl + "]]></comment_srl>\n"
					+ "<module><![CDATA[textyle]]></module>\n"
					+ "<act><![CDATA[procTextyleCommentItemDelete]]></act>\n"
					+ "<vid><![CDATA[" + textyle.domain
					+ "]]></vid>\n</params>\n</methodCall>";

			// send the request
			String response = KarybuHost.getINSTANCE().postRequest(
					"/index.php", xml);

			// parse the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try {
				confirmation = serializer.read(KarybuResponse.class, reader,
						false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return params[0];
		}

		// method called when the response came
		@Override
		protected void onPostExecute(KarybuComment comment) {
			// check if the comment was deleted successfuly
			if (confirmation.message.equals("Deleted successfully.")) {
				adapter.getArrayWithComments().remove(comment);
				adapter.notifyDataSetChanged();
			}

			super.onPostExecute(comment);
		}
	}

	// Async Task for changing the visibility
	private class VisibilityChangeAsyncTask extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			KarybuHost.getINSTANCE().postRequest("/index.php", params[0]);

			return null;
		}

	}
}
