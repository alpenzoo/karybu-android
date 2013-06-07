package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="post")
public class KarybuTextylePost 
{
	@Element
	public String document_srl;
	
	@Element
	public String module_srl;
	
	@Element
	public String category_srl;
	
	@Element
	public String title;
	
	@Element
	public int comment_count;
	
	@Element
	public String url;
	
	@Element
	public String status;
	
	@Override
	public String toString() 
	{
		return title;
	}
}
