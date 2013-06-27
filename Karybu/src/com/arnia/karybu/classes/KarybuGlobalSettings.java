package com.arnia.karybu.classes;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "response")
public class KarybuGlobalSettings extends KarybuSettings {
	@Element
	public String langs;

	@Element
	public String default_lang;

	@Element
	public String timezone;

	@Element(required = false)
	public String mobile;

	@Element(required = false)
	public String ips;

	@Element
	public String default_url;

	@Element(required = false)
	public String use_ssl;

	@Element
	public String rewrite_mode;

	@Element(required = false)
	public String use_sso;

	@Element(required = false)
	public String db_session;

	@Element(required = false)
	public String qmail;

	@Element(required = false)
	public String html5;

	public ArrayList<String> getSelectedLanguages() {
		String[] array = langs.split(":");

		ArrayList<String> returned = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			returned.add(array[i]);
		}

		return returned;
	}

}
