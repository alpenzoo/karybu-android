package com.arnia.karybu.pages;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.arnia.karybu.KarybuFragment;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuHost;
import com.arnia.karybu.classes.KarybuPage;

public class PageAdapter extends BaseAdapter {
	// array with pages that appear in listview
	private ArrayList<KarybuPage> arrayWithPages;

	private KarybuFragment context;

	public void setArrayWithPages(ArrayList<KarybuPage> arrayWithPages) {
		this.arrayWithPages = arrayWithPages;
	}

	public PageAdapter(KarybuFragment context) {
		arrayWithPages = new ArrayList<KarybuPage>();
		this.context = context;
	}

	@Override
	public int getCount() {
		return arrayWithPages.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// get the page from the array
		KarybuPage page = arrayWithPages.get(pos);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.cellview_page_item,
					null);
		}

		// construct the view's elements
		TextView menuItemNameTextView = (TextView) convertView
				.findViewById(R.id.PAGEITEMCELL_TEXTVIEW);
		menuItemNameTextView.setText(page.mid);

		TextView pageURL = (TextView) convertView
				.findViewById(R.id.PAGE_PAGE_URL);
		if (page.virtual_site == null) {
			pageURL.setText(KarybuHost.getINSTANCE().getURL() + "/index.php?mid="
					+ page.mid);
		} else {
			pageURL.setText(KarybuHost.getINSTANCE().getURL() + "/index.php?mid="
					+ page.mid + "&vid=" + page.virtual_site);
		}

		Button deleteButton = (Button) convertView
				.findViewById(R.id.PAGEITEMCELL_DELETEBUTTON);
		deleteButton.setTag(pos);
		deleteButton.setOnClickListener((OnClickListener) context);

		// return the view
		return convertView;
	}

}
