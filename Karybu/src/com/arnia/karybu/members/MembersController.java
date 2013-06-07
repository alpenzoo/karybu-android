package com.arnia.karybu.members;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuMember;

//Activity that has a list of members
public class MembersController extends KarybuFragment implements
		OnItemClickListener {
	// Array with the parsed response
	private KarybuArrayList arrayWithMembers = new KarybuArrayList();

	// the ListView
	private ListView listView;

	// adapter for the listView
	private ArrayAdapter<KarybuMember> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_members, container,
				false);

		listView = (ListView) view.findViewById(R.id.MEMBERS_LISTVIEW);
		//
		// //adapter for the listView
		adapter = new ArrayAdapter<KarybuMember>(this.activity,
				android.R.layout.simple_list_item_1);

		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		startProgress(activity, "Loading...");

		// send the request to get the members
		GetMembersAsync asyncRequest = new GetMembersAsync();
		asyncRequest.execute();
	}

	// called when an item in listView is pressed
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		KarybuMember member = arrayWithMembers.members.get(position);

		EditMemberController editMemberController = new EditMemberController();
		Bundle args = new Bundle();
		args.putSerializable("member", member);
		editMemberController.setArguments(args);
		((MainActivityController) activity)
				.addMoreScreen(editMemberController);
	}

	// AsyncTask to get all members
	private class GetMembersAsync extends AsyncTask<Void, Void, Void> {
		String xmlData;

		// make the request in background
		@Override
		protected Void doInBackground(Void... params) {
			// make the request
			xmlData = KarybuHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationDisplayMembers");

			// parse the response
			Serializer serializer = new Persister();

			Reader reader = new StringReader(xmlData);
			try {
				arrayWithMembers = serializer.read(KarybuArrayList.class, reader,
						false);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		// when the response is received update the adapter
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// dismiss the loading message
			dismissProgress();

			// add members in adapter
			if (arrayWithMembers.members != null) {
				// clear adapter
				adapter.clear();
				for (int i = 0; i < arrayWithMembers.members.size(); i++) {
					adapter.add(arrayWithMembers.members.get(i));
				}
				adapter.notifyDataSetChanged();
			}
		}

	}

}
