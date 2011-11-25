package hbot;


import java.util.*;
import java.util.regex.*;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;

public class Commands
{
	Sender sender;
	UserManager mngUser;
	private static String Mods;
	private static ArrayList<String> lstCommands;

	public Commands(Sender sender,UserManager mngUser)
	{
		lstCommands = new ArrayList<String>();
		this.sender=sender;
		this.mngUser=mngUser;
		if(lstCommands.isEmpty())
		{
			lstCommands.add("/salute");
			lstCommands.add("/invite");
			lstCommands.add("/online");
			lstCommands.add("/remove");
			lstCommands.add("/nick");
			lstCommands.add("/source");
			lstCommands.add("/setnick");
			lstCommands.add("/save");
			lstCommands.add("/load");
			lstCommands.add("/help");
			lstCommands.add("/snooze");
		}
		Mods = "/invite <e-mail>     {Invita un usuario al grupo}\r\n"+
					  "/online              {Muestra la lista de usuarios en el grupo}\r\n"+
					  "/remove <Nick>       {Remueve un usuario del grupo}\r\n"+
					  "/nick <Nick>         {Cambia el nick actual}\r\n"+
					  "/source              {Muestra la direccion del sourcecode del bot}\r\n"+
					  "/setnick <Old-Nick> <New-Nick>     {Cambia el nick de un usuario en especifico por otro}\r\n"+
					  "/save                {Guarda la lista de usuarios en el grupo}\r\n"+
					  "/load                {Carga la lista de usuarios del grupo}\r\n";
	}

	boolean isCommand(String msg)
	{
		String[] command=msg.split(" ");
	return lstCommands.contains(command[0]);
	}

	int run(User UserFrom,String msg) throws Exception
	{
		String[] args=msg.split(" ");
		switch(lstCommands.indexOf(args[0]))
		{
			case 0: //salute
				Salute();
			break;

			case 1: //invite
				if(UserFrom.isMod())
					Invite(args[1]);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 2: //online
				Online(UserFrom);
			break;

			case 3: //remove
				if(UserFrom.isMod())
					Remove(args[1]);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 4: //nick
				if(UserFrom.isMod())
					ChangeNick(UserFrom,args[1]);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 5: //source
				ShowSource(UserFrom);
			break;

			case 6: //setnick
				if(UserFrom.isMod())
					SetNick(args[1]);
				else
					PrintNoAccess(UserFrom);
				Save();
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
			
			case 9: //help
				Help(UserFrom);
			break;

			case 10: //snooze
				if (args[1].compareTo("on")==0)
				{	
					UserFrom.SetSnooze(true);
				}
				else if(args[1].compareTo("off")==0)
				{
					UserFrom.SetSnooze(false);
				}
				else
					sender.SendTo(UserFrom, "Fuck You! :p");
				//Save();
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
				DataManager DM = new DataManager(mngUser);
				DM.remove(msg);
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

	int Help(User UserFrom)
	{
		if(UserFrom.isMod())
		{
			sender.SendTo(UserFrom,"\n/salute     {Saluda a la comunidad}\n" +Mods+
					               "/help       {Muestra la ayuda del Bot}"+
					               "/snooze <on/off> {Activa y Desactiva la recepcion de mensajes}");
		}
		return 0;
	}
}
