package hbot;

import java.io.IOException;
import javax.servlet.http.*;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@SuppressWarnings("serial")
public class Main extends HttpServlet
{  
	public static UserManager mngUser=new UserManager();
	public static XMPPService xmpp=XMPPServiceFactory.getXMPPService();
	public static Sender sender=new Sender(xmpp,mngUser);
	public static Commands cmd=new Commands(sender,mngUser);
	DataManager DM = new DataManager(mngUser);
	
	public Main() throws Exception
	{		 
		cmd.Load();
	}
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{  
		Message msg = xmpp.parseMessage(req);
		User UserFrom=new User(msg.getFromJid());
		  
		//Identificación de usuario
		boolean InvitedUser=false;
		for(User u:mngUser.getUsers())
		{
			if(u.getAddr().compareTo(UserFrom.getAddr())==0)
			{
				UserFrom=u;
				InvitedUser=true;
			}
		}
		if(!InvitedUser) return;
		  
		/* Proceso del mensaje */
		String body=msg.getBody();
		if(cmd.isCommand(body))
		{
			try
			{
				cmd.run(UserFrom,body);
			}
			catch (Exception e)
			{
				return;
			}
		}
		else //Mensaje de texto, lo enviamos.
		{
			String response = "["+UserFrom.getNick()+"] "+body; 
			sender.SendEveryBodyFrom(UserFrom,response);
		}
	}
}
