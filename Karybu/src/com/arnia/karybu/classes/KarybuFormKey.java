package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="response")
public class KarybuFormKey 
{
	@Element
	public String form_key;
	
	@Element
	public String form_key_name;
	
}
