package com.arnia.karybu.classes;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="textyle")
public class KarybuTextyle implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525579434515035908L;

	@Element
	public String domain;
	
	@Element
	public String textyle_srl;
	
	@Element
	public String module_srl;
	
	@Element
	public String timezone;
	
	@Element
	public String default_lang;
	
	@Element
	public String user_id;
	
	@Element
	public String site_srl;
	
	@Element
	public String email_address;
	
	@Element
	public String use_mobile;
	
	@Element
	public String mid;
	
	@Element
	public String skin;
	
	@Element
	public String browser_title;
	
	@Element
	public String textyle_title;
	
	@Element (required=false)
	public String comment_count;
	
	
	@Override
	public String toString() 
	{
		return browser_title;
	}
}
