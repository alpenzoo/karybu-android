package com.arnia.karybu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.arnia.karybu.R;

public class WelcomeScreenController extends Activity implements
		OnClickListener {

	private Button addWebsite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_welcome_screen);

		this.addWebsite = (Button) findViewById(R.id.ADD_NEW_WEBSITE);
		this.addWebsite.setClickable(true);
		this.addWebsite.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent callLogin = new Intent(this, LoginController.class);
		startActivity(callLogin);
		finish();
	}

}
