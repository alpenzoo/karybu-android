package com.arnia.karybu.menus;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.arnia.karybu.R;
import com.arnia.karybu.classes.KarybuMenu;

//Adapter for Menu list
public class MenuAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<KarybuMenu> arrayWithMenus;
	
	//getter
	public ArrayList<KarybuMenu> getArrayWithMenus() 
	{
		return arrayWithMenus;
	}
	
	//setter
	public void setArrayWithMenus(ArrayList<KarybuMenu> arrayWithMenus) {
		this.arrayWithMenus = arrayWithMenus;
	}
	
	//constructor
	public MenuAdapter(Context context )
	{
		
		this.arrayWithMenus = new ArrayList<KarybuMenu>();
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		if( arrayWithMenus == null ) return 0;
			return arrayWithMenus.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return this.arrayWithMenus.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		KarybuMenu menu = this.arrayWithMenus.get(position);
		
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.cellview_menu, null);
		}
		
		TextView textView = (TextView) convertView.findViewById(R.id.MENUCELL_TEXTVIEW);
		textView.setText(menu.menuName);
		
		Button deleteButton = (Button) convertView.findViewById(R.id.MENUCELL_DELETEBUTTON);
		
//		deleteButton.setOnClickListener((OnClickListener) context);
		deleteButton.setTag(position);
		
		return convertView;
	}	
	
}
