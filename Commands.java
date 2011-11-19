package hbot;

import java.util.regex.*;
import com.google.appengine.api.xmpp.JID;

public class Commands
{
	Sender sender;
	UserManager mngUser;
	public Commands(Sender sender,UserManager mngUser)
	{
		this.sender=sender;
		this.mngUser=mngUser;
	}
	boolean isCommand(String msg)
	{		
		if(msg.startsWith("/salute")) return true;
		if(msg.startsWith("/invite")) return true;
		
		return false;
	}
	
	int run(User UserFrom,String msg)
	{
		if(msg.startsWith("/salute")) Salute();
		if(msg.startsWith("/invite")) Invite(msg);
		return 0;
	}
	
	int Salute()
	{
		sender.sendEverybody("[BOT] Hola, soy un bot :P");
		return 0;
	}
	
	int Invite(String msg)
	{
		msg=msg.substring(msg.indexOf(' '),msg.length());
//		Pattern email = Pattern.compile("^\\S+@\\S+$");
//		Matcher mt=email.matcher(msg);
//		if(mt.find())
//		{
			sender.sendEverybody("[BOT] Voy a hacer una invitación a "+msg+".");
			sender.Invite(msg);
			sender.sendEverybody("[BOT] La hize :P");
			//mngUser.addUser(new User(new JID(msg)));
			sender.sendEverybody("[BOT] "+msg+" ha sido invitado.");
//		}
		return 0;
	}
}
