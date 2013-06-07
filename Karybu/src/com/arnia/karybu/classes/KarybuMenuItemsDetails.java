package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root (name="response")
public class KarybuMenuItemsDetails 
{
	@Element
	@Path("menu_item[1]")
	public String name;
	
	@Element
	@Path("menu_item[1]")
	public String menu_item_srl;
	
	@Element
	@Path("menu_item[1]")
	public String open_window;
	
	@Element (required=false)
	@Path("menu_item[1]")
	public String url;
	
	@Element (required=false)
	@Path("menu_item[1]")
	public String moduleType;
	
	@Element (required=false)
	@Path("menu_item[1]")
	public String pageType;
}
