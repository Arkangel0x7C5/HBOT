package hbot;

import java.util.*;
import java.util.regex.*;
import com.google.appengine.api.xmpp.JID;

public class Commands
{
	Sender sender;
	UserManager mngUser;
	private static String ModCmds;
	private static String UsrCmds;
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
			lstCommands.add("/private");
		}
		ModCmds = "/invite <e-mail>     {Invita un usuario al grupo}\r\n"+
				"/online              {Muestra la lista de usuarios en el grupo}\r\n"+
				"/remove <Nick>       {Remueve un usuario del grupo}\r\n"+
				"/nick <Nick>         {Cambia el nick actual}\r\n"+
				"/setnick <Old-Nick> <New-Nick>     {Cambia el nick de un usuario en especifico por otro}\r\n"+
				"/save                {Guarda la lista de usuarios en el grupo}\r\n"+
				"/load                {Carga la lista de usuarios del grupo}\r\n";
		UsrCmds = "/help       {Muestra la ayuda del Bot}\n"+
				"\n/salute     {Saluda a la comunidad}\n"+
				"/source              {Muestra la direccion del sourcecode del bot}\r\n"+
				"/snooze <on/off> {Activa y Desactiva la recepcion de mensajes}\n";
	}

	boolean isCommand(String msg)
	{
		String[] command=msg.split(" ");
		return lstCommands.contains(command[0]);
	}

	int run(User UserFrom,String msg) throws Exception
	{
		String[] strArgs=msg.split(" ");
		ArrayList<String> args=new ArrayList<String>(Arrays.asList(strArgs));
		args.remove(0);
		
		switch(lstCommands.indexOf(strArgs[0]))
		{
			case 0: //salute
				Salute();
			break;
			
			case 1: //invite
				if(UserFrom.isMod())
					Invite(args);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 2: //online
				Online(UserFrom);
			break;

			case 3: //remove
				if(UserFrom.isMod())
					Remove(args);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 4: //nick
				if(UserFrom.isMod())
					ChangeNick(UserFrom,args);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 5: //source
				ShowSource(UserFrom);
			break;

			case 6: //setnick
				if(UserFrom.isMod())
					SetNick(args);
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
				if (args.get(0).compareTo("on")==0)
				{	
					UserFrom.SetSnooze(true);
				}
				else if(args.get(0).compareTo("off")==0)
				{
					UserFrom.SetSnooze(false);
				}
				else
					sender.SendTo(UserFrom, "Fuck You! :p");
				//Save();
			break;
			case 11: //Private <email> msg. Manda un mensage a una persona
				Private(args[1],msg.substring(msg.indexOf(args[1])+args[1].length()),UserFrom);
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
	
	@SuppressWarnings("deprecation")
	int Online(User UserFrom)
	{
		String lstUsuarios="";

		for(User u:mngUser.getUsers())
		{
			if(u.isMod())
				lstUsuarios+="[+]";
			else
				lstUsuarios+="[-]";

			lstUsuarios+="["+u.getNick()+"]\t";
			//Si no es administrador, no se muestran los mails
			if(UserFrom.isMod())lstUsuarios+="<"+u.getAddr()+">\t";
			
			if(sender.xmpp.getPresence(new JID(u.getAddr())).isAvailable())lstUsuarios+="conectado";
			lstUsuarios+="\n";
		}

		sender.SendTo(UserFrom,"[BOT]\n"+lstUsuarios);
		return 0;
	}

	int Invite(ArrayList<String> args)
	{
		if(args.size()!=1) return -1;
		
		Pattern email = Pattern.compile("^\\S+@\\S+\\.\\S+$");
		Matcher mt=email.matcher(args.get(0));
		if(mt.find())
		{	
			sender.Invite(args.get(0).trim());
			User nUser=new User(new JID(args.get(0).trim()+"/"));
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

	int Remove(ArrayList<String> args)
	{
		if(args.size()!=1) return -1;

		Pattern email = Pattern.compile("^\\S+@\\S+\\.\\S+$");
		Matcher mt=email.matcher(args.get(0).trim());
		if(mt.find())
		{
			if(mngUser.removeUser(args.get(0).trim())==0)
			{
				DataManager DM = new DataManager(mngUser);
				DM.remove(args.get(0).trim());
				sender.sendEverybody("[BOT] "+args.get(0).trim()+" eliminado.");
			}
			else
			{
				sender.sendEverybody("[BOT] Error al eliminar al usuario "+args.get(0).trim()+".");
			}
		}

		return 0;
	}

	int ChangeNick(User user,ArrayList<String> args)
	{
		sender.SendTo(user,args.toString());
		if(args.size()!=1) return -1;
		
		String oldNick=user.getNick();
		user.setNick(args.get(0).trim());
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

	int SetNick(ArrayList<String> args)
	{
		if(args.size()!=2) return -1;

		for(User u:mngUser.getUsers())
		{
			if(u.getAddr().compareTo(args.get(0).trim())==0 && (!u.isMod()))
			{
				String oldNick=u.getNick();
				u.setNick(args.get(1).trim());
				sender.sendEverybody("[BOT] "+oldNick+" es ahora conocido como "+u.getNick());
				break;
			}
		}
		return 0;
	}

	int Help(User UserFrom)
	{
		String strHelp = UsrCmds;
		if(UserFrom.isMod()) strHelp+=ModCmds;
		sender.SendTo(UserFrom,strHelp);
		
		return 0;
	}

	int Private(String nick,String msg,User From)
	{
		return 0;
	}
}
