package com.arnia.karybu.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "response")
public class KarybuResponse {
	@Element(required = false)
	public String value;

	@Element(required = false)
	public int error;
	
	@Element(required = false)
	public String message;

	@Element(required = false)
	public String document_srl;
}
