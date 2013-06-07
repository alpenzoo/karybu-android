package com.arnia.karybu.classes;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="menu")
public class KarybuMenu 
{
	@Element
	public String menuName;
	
	@Element
	public String menuSrl;
	
	@ElementList(inline=true,required=false)
	public ArrayList<KarybuMenuItem> menuItems;
}
