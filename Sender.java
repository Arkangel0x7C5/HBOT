package hbot;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;

public class Sender
{

	UserManager mngUser;
	public XMPPService xmpp;
	
	Sender(XMPPService xmpp,UserManager mngUser)
	{
		this.mngUser=mngUser;
		this.xmpp=xmpp;
	}
	
	@SuppressWarnings("deprecation")
	int sendEverybody(String msg)
	{
		  for(User user:mngUser.getUsers())
		  {
			  JID jid=new JID(user.getAddr());
				  if(xmpp.getPresence(jid).isAvailable()&&!user.isSnoozing())
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
	
	@SuppressWarnings("deprecation")
	int SendEveryBodyFrom(User UserFrom,String msg)
	{
		  for(User user:mngUser.getUsers())
		  {
			  if(user.getAddr().compareTo(UserFrom.getAddr())!=0 && !user.isSnoozing())
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
	
	@SuppressWarnings("deprecation")
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
