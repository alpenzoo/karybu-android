package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="day")
public class KarybuDayStats 
{
	@Element
	public String date;
	
	@Element
	public String unique_visitor;
	
	@Element
	public String pageview;
}
