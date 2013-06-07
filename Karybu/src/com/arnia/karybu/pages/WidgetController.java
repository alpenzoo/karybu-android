package com.arnia.karybu.pages;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;

public class WidgetController extends Activity {
	private WebView webView;
	private String pageMid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_widget);

		pageMid = getIntent().getStringExtra("mid");

		// get vid for virtual site
		String vid = "";
		if (getIntent().getExtras().containsKey("vid")) {
			vid = getIntent().getExtras().getString("vid");
		}

		webView = (WebView) findViewById(R.id.WIDGET_WEBVIEW);

		// setCookies();
		if (vid.compareTo("") == 0) {
			webView.loadUrl(KarybuHost.getINSTANCE().getURL() + "/index.php?mid="
					+ pageMid);
		} else {
			webView.loadUrl(KarybuHost.getINSTANCE().getURL() + "/index.php?mid="
					+ pageMid + "&vid=" + vid);
		}
	}
}
