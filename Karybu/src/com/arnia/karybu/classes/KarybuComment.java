package com.arnia.karybu.classes;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="comment")
public class KarybuComment implements Comparable<KarybuComment>
{
	@Element
	public String comment_srl;
	
	@Element
	public String module_srl;
	
	@Element
	public String document_srl;
	
	@Element
	public String status;
	
	@Element
	public String content;
	
	@Element
	public String nickname;
	
	@Element(required=false)
	public String email;
	
	@Element(required=false)
	public String homepage;
	
	@Element(required=false)
	public String ipaddress;
	
	@Element
	public String parent_srl;
	
	@Element
	public String regdate;
	
	private Date date;
	
	@SuppressLint("SimpleDateFormat")
	public Date getDate() 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    	try {
			date = sdf.parse(regdate);
		} catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		return date;
	}

	@Override
	public int compareTo(KarybuComment another) 
	{
		if( another.getDate().compareTo( this.getDate() ) > 0 ) return -1;
		else if( another.getDate().compareTo(this.getDate() ) < 0 ) return 1;
		return 0;
	}
	
}
