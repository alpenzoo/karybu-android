package com.arnia.karybu.classes;

import java.util.HashMap;
import java.util.Map;

public class KarybuSettings 
{
	protected HashMap<String, String> languages;
	protected HashMap<String, String> zones;

	
	public HashMap<String, String> getLanguages() 
	{
		if( languages == null )
		{
		languages = new HashMap<String, String>();
		
		languages.put("en", "English");
		languages.put("ko", "한국어");
		languages.put("jp", "日本語");
		languages.put("zh-CN","中文(中国)");
		languages.put("zh-TW", "中文(臺�?�)");
		languages.put("fr", "Francais");
		languages.put("de", "Deutsch");
		languages.put("ru","Ру�?�?кий");
		languages.put("es","Español");
		languages.put("tr","Türkçe");
		languages.put("vi","Tiếng Việt");
		languages.put("mn", "Mongolian");
		}
		
		return languages;
	}

	public HashMap<String, String> getZones() 
	{
		if( zones == null )
		{
		zones = new HashMap<String, String>();
		zones.put("-1200", "GMT -12:00");
		zones.put("-1100", "GMT -11:00");
		zones.put("-1000", "GMT -10:00");
		zones.put("-0900", "GMT -09:00");
		zones.put("-0800", "GMT -08:00");
		zones.put("-0700", "GMT -07:00");
		zones.put("-0600", "GMT -06:00");
		zones.put("-0500", "GMT -05:00");
		zones.put("-0400", "GMT -04:00");
		zones.put("-0300", "GMT -03:00");
		zones.put("-0200", "GMT -02:00");
		zones.put("-0100", "GMT -01:00");
		zones.put("0000", "GMT 00:00");
		zones.put("+0100", "GMT +01:00");
		zones.put("+0200", "GMT +02:00");
		zones.put("+0300", "GMT +03:00");
		zones.put("+0400", "GMT +04:00");
		zones.put("+0500", "GMT +05:00");
		zones.put("+0600", "GMT +06:00");
		zones.put("+0700", "GMT +07:00");
		zones.put("+0800", "GMT +08:00");
		zones.put("+0900", "GMT +09:00");
		zones.put("+1000", "GMT +10:00");		
		zones.put("+1100", "GMT +11:00");
		zones.put("+1200", "GMT +12:00");
		zones.put("+1300", "GMT +13:00");
		zones.put("+1400", "GMT +14:00");
		}
		return zones;
	}
	
	public String getZoneWithKey(String key)
	{
		return zones.get(key);
	}
	
	public String getKeyWithZone(String zone)
	{
		for(Map.Entry<String, String> entry : zones.entrySet())
		{
			if( entry.getValue().equals(zone) ) return entry.getKey();
		}
		
		return "";
	}
	
	public String getLanguageWithKey(String key)
	{
		return getLanguages().get(key);
	}
	
	public String getKeyWithLanguage(String lang)
	{
		for(Map.Entry<String, String> entry : languages.entrySet())
		{
			if( entry.getValue().equals(lang) ) return entry.getKey();
		}
		
		return "";
	}
}
