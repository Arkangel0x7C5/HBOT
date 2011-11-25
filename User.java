package hbot;

import com.google.appengine.api.xmpp.JID;

public class User
{
	private String Nick;
	private String Addr;
	private boolean snooze;
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
		
		if(Addr.compareTo("zero@h-sec.org")==0 ||
			Addr.compareTo("lordrna@h-sec.org")==0 ||
			Addr.compareTo("aperezhrd@gmail.com")==0 ||
			Addr.compareTo("arkangelhacket@gmail.com")==0)
		{
			Moderator=true;
		}
		else
		{
			Moderator=false;
		}
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
	
	public boolean isMod()
	{
		return Moderator;
	}

	public void SetSnooze(boolean status)
	{
		snooze=status;
	}
	
	public boolean isSnoozing()
	{
		return snooze;
	}
}
