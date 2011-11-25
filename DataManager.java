package hbot;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.IOException;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.xmpp.JID;


// �� AQU� HAY MUCHO QUE ARREGLAR !!
//		(pero ahora tengo use�o)

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
		String strURL="http://foro.h-sec.org/bot.php?key={pass}&value=";
		
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
		HTTPRequest request = new HTTPRequest(new URL("http://foro.h-sec.org/bot.php?key={pass}"));
		request.setHeader(new HTTPHeader("Cache-Control", "no-cache,max-age=0"));
		request.setHeader(new HTTPHeader("Pragma", "no-cache"));
		URLFetchService urlFetchService=URLFetchServiceFactory.getURLFetchService();
		HTTPResponse resp=urlFetchService.fetch(request);
	
		String Buffer=new String(resp.getContent());
		
            
		String[] lstUsers=Buffer.split(" ");
		 
		//No es necesario, no se a�aden duplicados
		//mngUser.getUsers().clear();
		 
		for(String s:lstUsers)
		{
			mngUser.addUser(new User(new JID(s.trim()+"/")));
		}
 
		Main.sender.sendEverybody("[BOT] "+lstUsers.length+" contactos a�adidos.");
 
		return 0;
	}
}
