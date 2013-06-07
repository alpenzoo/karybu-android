package com.arnia.karybu.classes;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="menuItem")
public class KarybuMenuItem 
{
	@Element
	public String menuItemName;
	
	@Element
	public String srl;
	
	@Element
	public String open_window;
	
	@Element(required=false)
	public String url;
	
	@ElementList(inline=true,required=false)
	public ArrayList<KarybuMenuItem> menuItems;
}
