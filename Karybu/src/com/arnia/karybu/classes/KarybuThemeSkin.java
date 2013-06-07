package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="skin")
public class KarybuThemeSkin
{
	@Element
	public String module;
	
	@Element
	public String name;	
	
}
