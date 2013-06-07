package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="response")
public class KarybuTextyleSettings extends KarybuSettings
{
	public String blogTitle;
	public String language;
	public String timezone;
	
	@Element
	public String editor;
	
	@Element(required=false)
	public String fontFamily;
	
	@Element(required=false)
	public String fontSize;
	
	@Element
	public String usePrefix;
	
	@Element(required=false)
	public String prefix;
	
	@Element
	public String useSuffix;
	
	@Element(required=false)
	public String suffix;
	
	public String getPrefix() 
	{
		if( prefix == null ) return "";
		return prefix;
	}
	
	public String getSuffix() 
	{
		if( prefix == null ) return "";
		return suffix;
	}
	
	public String getFontFamily() 
	{
		if( fontFamily == null ) return "";
		return fontFamily;
	}
	
	public String getFontSize()
	{
		if( fontSize == null ) return "";
		return fontSize;
	}
}
