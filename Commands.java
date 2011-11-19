package hbot;

import java.util.regex.*;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;

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
		if(msg.startsWith("/online")) return true;
		if(msg.startsWith("/remove")) return true;
		if(msg.startsWith("/nick")) return true;
		
		return false;
	}
	
	int run(User UserFrom,String msg)
	{
		if(msg.startsWith("/salute"))
		{
			Salute();
		}
		if(msg.startsWith("/invite"))
		{
			if(UserFrom.isMod())
			{
				Invite(msg.substring(msg.indexOf(' '),msg.length()));
			}
			else
			{
				PrintNoAccess(UserFrom);
			}
		}
		if(msg.startsWith("/online"))
		{
			Online();
		}
		if(msg.startsWith("/remove"))
		{
			if(UserFrom.isMod())
			{
				Remove(msg.substring(msg.indexOf(' '),msg.length()));
			}
			else
			{
				PrintNoAccess(UserFrom);
			}
		}
		if(msg.startsWith("/nick")) 
		{
			if(UserFrom.isMod())
			{
				ChangeNick(UserFrom,msg.substring(msg.indexOf(' '),msg.length()));
			}
			else
			{
				PrintNoAccess(UserFrom);
			}
		}
		
		return 0;
	}
	
	int Salute()
	{
		sender.sendEverybody("[BOT] Hola, soy un bot :P");
		return 0;
	}
	
	int PrintNoAccess(User user)
	{
		//TODO
		return 0;
	}
	
	int Online()
	{
		String lstUsuarios="";
		
		for(User u:mngUser.getUsers())
		{
			if(u.isMod())
				lstUsuarios+="[+]";
			else
				lstUsuarios+="[-]";
			
			lstUsuarios+="["+u.getNick()+"]"+"<"+u.getAddr()+">\n";
		}
		
		sender.sendEverybody("[BOT]\n"+lstUsuarios);
		return 0;
	}
	
	int Invite(String msg)
	{
		msg=msg.trim();
		Pattern email = Pattern.compile("^\\S+@\\S+\\.\\S+$");
		Matcher mt=email.matcher(msg);
		if(mt.find())
		{	
			sender.Invite(msg);
			User nUser=new User(new JID(msg+"/"));
			if(mngUser.addUser(nUser)==0)
			{
				sender.sendEverybody("[BOT] "+nUser.getAddr()+" ha sido invitado.");
			}
			else
			{
				sender.sendEverybody("[BOT] "+nUser.getAddr()+" ya existe.");
			}
		}
		return 0;
	}
	
	int Remove(String msg)
	{
		msg=msg.trim();
		
		Pattern email = Pattern.compile("^\\S+@\\S+\\.\\S+$");
		Matcher mt=email.matcher(msg);
		if(mt.find())
		{
			if(mngUser.removeUser(msg)==0)
			{
				sender.sendEverybody("[BOT] "+msg+" eliminado.");
			}
			else
			{
				sender.sendEverybody("[BOT] Error al eliminar al usuario "+msg+".");
			}
		}
		
		return 0;
	}
	
	int ChangeNick(User user,String nick)
	{
		nick=nick.trim().replace(" ","");
		String oldNick=user.getNick();
		user.setNick(nick);
		sender.sendEverybody("[HBOT] "+oldNick+" es ahora conocido como "+user.getNick());
		return 0;
	}
}
