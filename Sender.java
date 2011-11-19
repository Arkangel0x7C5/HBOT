package hbot;

import java.io.IOException;
import javax.servlet.http.*;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import java.util.*;

public class Sender
{
	UserManager mngUser;
	XMPPService xmpp;
	
	Sender(XMPPService xmpp,UserManager mngUser)
	{
		this.mngUser=mngUser;
		this.xmpp=xmpp;
	}
	
	int sendEverybody(String msg)
	{
		  for(User user:mngUser.getUsers())
		  {
			  JID jid=new JID(user.getAddr());
				  if(xmpp.getPresence(jid).isAvailable())
				  {
					  Message message = new MessageBuilder()
			  			.withRecipientJids(jid)
			  			.withBody(msg)
			  			.build();
			  
					  xmpp.sendMessage(message);
				  }
		  }		  
		  return 0;
	}
	
	int SendEveryBodyFrom(User UserFrom,String msg)
	{
		  for(User user:mngUser.getUsers())
		  {
			  if(user.getAddr().compareTo(UserFrom.getAddr())!=0)
			  {
				  JID jid=new JID(user.getAddr());
				  if(xmpp.getPresence(jid).isAvailable())
				  {
					  Message message = new MessageBuilder()
			  			.withRecipientJids(jid)
			  			.withBody(msg)
			  			.build();
			  
					  xmpp.sendMessage(message);
				  }
			  }
		  }
		return 0;
	}
	
	int SendTo(User UserTo,String msg)
	{
		JID jid=new JID(UserTo.getAddr());
		if(xmpp.getPresence(jid).isAvailable())
		{
			Message message = new MessageBuilder()
			.withRecipientJids(jid)
			.withBody(msg)
			.build();
		  
			xmpp.sendMessage(message);
		}
		  
		return 0;
	}
	
	int Invite(String Addr)
	{
		xmpp.sendInvitation(new JID(Addr));
		return 0;
	}
}
