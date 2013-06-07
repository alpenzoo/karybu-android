package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="document")
public class KarybuDocument
{
	@Element
	public String alias;
	
	@Element
	public String content;	
	
}
