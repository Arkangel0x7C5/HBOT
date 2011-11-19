package hbot;

import com.google.appengine.api.xmpp.JID;

public class User
{
	private String Nick;
	private String Addr;
	boolean Moderator;
	
	public User(JID jid)
	{
		String strJID=jid.getId();
		if(strJID.indexOf("<JID: ")>=0)
		{
			strJID.replace("<JID: ","");
		}
		Nick=strJID.substring(0,strJID.indexOf('@'));
		Addr=strJID.substring(0,strJID.indexOf('/'));
		
//		if(Addr.compareTo("zero@h-sec.org")==0)
//		{
//			Moderator=true;
//		}
//		else
//			
//		{
//			Moderator=false;
//		}
	}

	public String getNick()
	{
		return Nick;
	}

	public void setNick(String nick)
	{
		Nick = nick;
	}

	public String getAddr()
	{
		return Addr;
	}

	public void setAddr(String addr)
	{
		Addr = addr;
	}
}
