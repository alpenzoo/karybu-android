package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="textylePage")
public class KarybuTextylePage 
{
	@Element
	public String module;
	
	@Element
	public String name;
	
	@Element
	public String type;
	
	@Element
	public String module_srl;
	
	@Element
	public String mid;
	
}
