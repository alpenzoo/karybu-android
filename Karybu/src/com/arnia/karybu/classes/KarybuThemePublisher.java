package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="publisher")
public class KarybuThemePublisher
{
	@Element
	public String name;
	
	@Element
	public String email;	
	
}
