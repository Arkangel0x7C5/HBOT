package hbot;

import java.util.regex.*;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;

public class Commands
{
	Sender sender;
	UserManager mngUser;
	String[] lstCommands={"salute",
							"invite",
							"online",
							"remove",
							"nick",
							"source",
							"setnick",
							"save",
							"load"};
	public Commands(Sender sender,UserManager mngUser)
	{
		this.sender=sender;
		this.mngUser=mngUser;
	}
	boolean isCommand(String msg)
	{		
		int i;
		for(i=0;i<lstCommands.length;i++)
		{
			if(msg.startsWith("/"+lstCommands[i]))
			{
				break;
			}
		}
		
		if(i==lstCommands.length) return false;
		else return true;
	}
	
	int run(User UserFrom,String msg) throws Exception
	{
		int i;
		for(i=0;i<lstCommands.length;i++)
		{
			if(msg.startsWith("/"+lstCommands[i]))
			{
				break;
			}
		}
		
		switch(i)
		{
			case 0: //salute
				Salute();
			break;
			
			case 1: //invite
				if(UserFrom.isMod())
					Invite(msg.substring(msg.indexOf(' '),msg.length()));
				else
					PrintNoAccess(UserFrom);
			break;
			
			case 2: //online
				Online(UserFrom);
			break;
			
			case 3: //remove
				if(UserFrom.isMod())
					Remove(msg.substring(msg.indexOf(' '),msg.length()));
				else
					PrintNoAccess(UserFrom);
			break;
			
			case 4: //nick
				if(UserFrom.isMod())
					ChangeNick(UserFrom,msg.substring(msg.indexOf(' '),msg.length()));
				else
					PrintNoAccess(UserFrom);
			break;
			
			case 5: //source
				ShowSource(UserFrom);
			break;
			
			case 6: //setnick
				if(UserFrom.isMod())
					SetNick(msg.substring(msg.indexOf(' '),msg.length()));
				else
					PrintNoAccess(UserFrom);
			break;
			
			case 7: //save
				if(UserFrom.isMod())
					Save();
				else
					PrintNoAccess(UserFrom);
			break;
			
			case 8: //load
				if(UserFrom.isMod())
					Load();
				else
					PrintNoAccess(UserFrom);
			break;
		}
		
		return 0;
	}
	
	int Salute()
	{
		sender.sendEverybody("[BOT] Hola, soy un bot :P");
		return 0;
	}
	
	int ShowSource(User UserTo)
	{
		sender.SendTo(UserTo,"[BOT] Código disponible en < https://github.com/hzeroo/HBOT >");
		return 0;
	}
	
	int PrintNoAccess(User user)
	{
		sender.SendTo(user,"[BOT] No tienes permisos para hacer esta operación.");
		return 0;
	}
	
	int Online(User UserFrom)
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

		sender.SendTo(UserFrom,"[BOT]\n"+lstUsuarios);
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
	
	int Save() throws Exception
	{
		DataManager DM=new DataManager(mngUser);
		
		DM.Save();
		
		return 0;
	}
	
	int Load() throws Exception
	{
		DataManager DM=new DataManager(mngUser);
		
		DM.Load();
		
		return 0;
	}
	
	int SetNick(String msg)
	{
		String[] args=msg.split(" ");
		if(args.length<3) return -1;

		for(User u:mngUser.getUsers())
		{
			if(u.getAddr().compareTo(args[1])==0 && (!u.isMod()))
			{
				String oldNick=u.getNick();
				u.setNick(args[2]);
				sender.sendEverybody("[BOT] "+oldNick+" es ahora conocido como "+u.getNick());
				break;
			}
		}
		
		
		
		return 0;
	}
}
