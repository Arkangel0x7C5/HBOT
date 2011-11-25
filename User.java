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
	
	public void SetMod(boolean status)
	{
		Moderator = status;
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