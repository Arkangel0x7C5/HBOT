package hbot;

import java.util.*;
import com.google.appengine.api.xmpp.JID;

public class UserManager
{
	private List<User> lstUsers;
	
	public UserManager()
	{
		lstUsers=new ArrayList<User>();
	}
	
	public int addUser(User user)
	{
		lstUsers.add(user);
		return 0;
	}
	
	public int addUser(JID jid)
	{
		lstUsers.add(new User(jid));
		return 0;
	}
	
	public List<User> getUsers()
	{
		return this.lstUsers;
	}
}
