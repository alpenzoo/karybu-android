package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="page")
public class KarybuPage 
{

	@Element
	public String module_srl;
	
	@Element
	public String module;
	
	@Element
	public String page_type;
	
	@Element
	public String mid;
	
	@Element (required=false)
	public String content;
	
	@Element (required=false)
	public String document_srl;
	
	@Element
	public String browser_title;
	
	@Element
	public String layout_srl;
	
	@Element(required=false)
	public String virtual_site;
}
