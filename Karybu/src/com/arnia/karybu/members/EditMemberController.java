package com.arnia.karybu.members;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.MainActivityController;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuMember;
import com.arnia.karybu.classes.KarybuResponse;

public class EditMemberController extends KarybuFragment implements
		OnClickListener {
	// ui references
	private TextView emailTextView;
	private EditText nicknameEditText;
	private EditText descriptionEditText;
	private CheckBox allowMailingCheckBox;
	private CheckBox allowMessageCheckBox;
	private CheckBox approveMemberCheckBox;
	private CheckBox isAdminCheckBox;
	private Button saveButton;

	// member that is edited
	private KarybuMember member;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_edit_member,
				container, false);

		// take references to UI elements
		emailTextView = (TextView) view
				.findViewById(R.id.EDITMEMBER_EMAIL);
		nicknameEditText = (EditText) view
				.findViewById(R.id.EDITMEMBER_NICKNAME);
		descriptionEditText = (EditText) view
				.findViewById(R.id.EDITMEMBER_DESCRIPTION);
		allowMailingCheckBox = (CheckBox) view
				.findViewById(R.id.EDITMEMBER_ALLOWMAILING);
		allowMessageCheckBox = (CheckBox) view
				.findViewById(R.id.EDITMEMBER_ALLOWMESSAGE);
		approveMemberCheckBox = (CheckBox) view
				.findViewById(R.id.EDITMEMBER_APPROVEMEMBER);
		isAdminCheckBox = (CheckBox) view
				.findViewById(R.id.EDITMEMBER_ISADMIN);
		saveButton = (Button) view.findViewById(R.id.EDITMEMBER_SAVE);
		saveButton.setOnClickListener(this);

		Bundle args = getArguments();

		member = (KarybuMember) args.getSerializable("member");

		// load the current settings
		completeSettingsFormWithMemberSettings();
		// }

		return view;
	}

	// load the current settings
	private void completeSettingsFormWithMemberSettings() {
		emailTextView.setText(member.email);
		nicknameEditText.setText(member.nickname);
		descriptionEditText.setText(member.description);

		if (member.allowMailing())
			allowMailingCheckBox.setChecked(true);
		else
			allowMessageCheckBox.setChecked(false);
		if (member.allowMessage())
			allowMessageCheckBox.setChecked(true);
		else
			allowMessageCheckBox.setChecked(false);
		if (member.isAdmin())
			isAdminCheckBox.setChecked(true);
		else
			isAdminCheckBox.setChecked(false);
		if (member.isApproved())
			approveMemberCheckBox.setChecked(true);
		else
			approveMemberCheckBox.setChecked(false);
	}

	// Method called when the save button is pressed
	@Override
	public void onClick(View v) {
		startProgress(activity, "Loading...");
		SaveMemberAsyncTask asyncTask = new SaveMemberAsyncTask();
		asyncTask.execute();
	}

	// AsyncTask for saving member details
	private class SaveMemberAsyncTask extends AsyncTask<Object, Object, Object> {
		KarybuResponse responseObj;
		String response;

		@Override
		protected Object doInBackground(Object... paramss) {
			//
			// building the request for saving the member
			//
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("ruleset", "insertAdminMember");
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationEditMember");
			params.put("member_srl", member.member_srl);
			params.put("email_address", member.email);
			params.put("password", member.password);
			params.put("nick_name", nicknameEditText.getText().toString());
			params.put("description", descriptionEditText.getText().toString());
			params.put("find_account_answer",
					(member.secret_answer == null) ? "" : member.secret_answer);
			params.put("find_account_question", member.find_account_question);
			if (isAdminCheckBox.isChecked())
				params.put("is_admin", "Y");
			else
				params.put("is_admin", "N");

			if (allowMailingCheckBox.isChecked())
				params.put("allow_mailing", "Y");
			else
				params.put("allow_mailing", "N");

			if (allowMessageCheckBox.isChecked())
				params.put("allow_message", "Y");
			else
				params.put("allow_message", "N");

			if (!approveMemberCheckBox.isChecked())
				params.put("denied", "Y");
			else
				params.put("denied", "N");

			// sending the request
			try {
				response = KarybuHost.getINSTANCE().postMultipart(params, "/");

				Serializer serializer = new Persister();
				Reader reader = new StringReader(response);

				responseObj = serializer.read(KarybuResponse.class, reader, false);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return responseObj;
		}

		// method called when the response came
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();

			((MainActivityController) activity).backwardScreen();
		}

	}
}
