package hbot;

import java.io.IOException;
import javax.servlet.http.*;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import java.util.*;

@SuppressWarnings("serial")
public class Main extends HttpServlet
{  
	public static final UserManager mngUser=new UserManager();
	public static final XMPPService xmpp=XMPPServiceFactory.getXMPPService();
	public static final Sender sender=new Sender(xmpp,mngUser);
	public static final Commands cmd=new Commands(sender,mngUser);
	
	public Main()
	{		
		mngUser.addUser(new User(new JID("zero@h-sec.org/")));
		mngUser.addUser(new User(new JID("lordrna@h-sec.org/")));
		mngUser.addUser(new User(new JID("arkangelhacket@gmail.com/")));
	}
	  @Override
	  public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException
	  {  
		  Message msg = xmpp.parseMessage(req);
		  User UserFrom=new User(msg.getFromJid());
		  String body = msg.getBody();
		  
		  if(cmd.isCommand(msg.getBody()))
		  {
			  cmd.run(UserFrom,msg.getBody());
		  }
		  else
		  {
			  String response = "["+UserFrom.getNick()+"]"+body; 
			  sender.SendEveryBodyFrom(UserFrom,response);
		  }
	  }
}