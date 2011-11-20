package hbot;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.google.appengine.api.xmpp.JID;


// ¡¡ AQUÍ HAY MUCHO QUE ARREGLAR !!
//		(pero ahora tengo useño)

public class DataManager
{
	UserManager mngUser;
	
	DataManager(UserManager mngUser)
	{
		this.mngUser=mngUser;
	}
	
	public int Save() throws Exception
	{
		Main.sender.sendEverybody("[BOT] Guardando contactos...");
		String strURL="http://foro.h-sec.org/bot.php?key={contraseña}&value=";
		
		for(User u:mngUser.getUsers())
		{
			strURL+=u.getAddr()+"%20";
		}
		
		 URL url = new URL(strURL);
         BufferedReader reader=new BufferedReader(new InputStreamReader(url.openStream()));
         String line;
         String Buffer="";
         while ((line=reader.readLine())!= null)
         {
        	 Buffer+=line;
         }
         reader.close();
         
         if(Buffer.contains("OK"))
         {
        	 Main.sender.sendEverybody("[BOT] "+mngUser.getUsers().size()+" contactos guardados correctamente.");
         }
         
        return 0;
	}
	
	public int Load() throws Exception
	{		
		Main.sender.sendEverybody("[BOT] Cargando contactos...");
		 URL url = new URL("http://foro.h-sec.org/bot.php?key={contraseña}");
         BufferedReader reader=new BufferedReader(new InputStreamReader(url.openStream()));
         String line;
         String Buffer="";
         while ((line=reader.readLine())!=null)
         {
        	 Buffer+=line;
         }
         reader.close();
         
                 
         String[] lstUsers=Buffer.split("\\s");
         
         //mngUser.getUsers().clear();
         
         for(String s:lstUsers)
         {
        	 mngUser.addUser(new User(new JID(s+"/")));
         }
         
         Main.sender.sendEverybody("[BOT] "+lstUsers.length+" contactos añadidos.");
         
		return 0;
	}
}
