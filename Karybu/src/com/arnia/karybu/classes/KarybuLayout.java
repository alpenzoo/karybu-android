package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="layout")
public class KarybuLayout 
{
	
	@Element
	public String layout_srl;
	
	@Element
	public String layout_name;
	
	@Element
	public String title;
}
