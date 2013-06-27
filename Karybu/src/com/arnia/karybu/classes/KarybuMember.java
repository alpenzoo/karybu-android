package com.arnia.karybu.classes;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="user")
public class KarybuMember implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Element
	public String nickname;
	
	@Element
	public String password;
	
	@Element
	public String member_srl;
	
	@Element
	public String denied;
	
	@Element
	public String user_id;
	
	@Element
	public String email;
	
	@Element
	public String allow_mailing;
	
	@Element
	public String allow_message;
	
	@Element(required=false)
	public String description;
	
	@Element(required=false)
	public String find_account_question;
	
	@Element(required=false)
	public String secret_answer;
	
	@Element
	public String is_admin;

	@Override
	public String toString() 
	{
		return this.email;
	}
	
	public boolean allowMailing()
	{
		if( this.allow_mailing.equals("Y") ) return true;
		else return false;
	}
	
	public boolean allowMessage()
	{
		if( this.allow_message.equals("Y") ) return true;
		else return false;
 	}
	
	public boolean isAdmin()
	{
		if( this.is_admin.equals("Y") ) return true;
		else return false;
	}

	public boolean isApproved()
	{
		if( this.denied.equals("Y") ) return false;
		else return true;
	}
}
