package com.arnia.karybu.data;

public class KarybuSite {
	public long id ;
	public String siteUrl;
	public String userName;
	public String password;
	
	public KarybuSite(long id, String siteUrl, String username, String password){
		this.id = id;
		this.siteUrl = siteUrl;
		this.userName = username;
		this.password = password;
	}
	@Override
	public String toString() {
	
		return this.siteUrl;
	}
}
