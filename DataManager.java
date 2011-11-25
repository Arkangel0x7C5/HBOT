package hbot;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

import com.google.appengine.api.xmpp.JID;

public class DataManager
{
	UserManager mngUser;
	
	DataManager(UserManager mngUser)
	{
		this.mngUser=mngUser;
	}
	
	public int Save() throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("users");
		PreparedQuery pq = datastore.prepare(q);
		boolean nuevo = false;
		User NewUser=null;
		for(User u: mngUser.getUsers())
		{
			for(Entity result: pq.asIterable())
			{
				NewUser=u;
				if(result.getProperty("email").toString().compareTo(u.getAddr())==0)
				{
				result.setProperty("Nick", u.getNick());
				if(u.isSnoozing())result.setProperty("Snooze", "true");
				else result.setProperty("Snooze", "false");
				if(u.isMod())result.setProperty("Mod", "true");
				else result.setProperty("Mod", "false");
				datastore.put(result);
				nuevo = false;
				break;
				}
				else nuevo = true;
			}		
			if(nuevo)AddUser(NewUser);
		}
		return 0;
	}
	
	public int Load() throws Exception
	{		  
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("users");
		PreparedQuery pq = datastore.prepare(q);
				
		for(Entity result: pq.asIterable())
		{
			mngUser.addUser(new User(new JID(result.getProperty("email").toString()+"/")));
			for(User u: mngUser.getUsers())
			{
				if(result.getProperty("email").toString().compareTo(u.getAddr())==0)
				{
				u.setNick(result.getProperty("Nick").toString());
				if(result.getProperty("Snooze").toString().compareTo("true")==0)u.SetSnooze(true);
				else u.SetSnooze(false);
				if(result.getProperty("Mod").toString().compareTo("true")==0)u.SetMod(true);
				else u.SetMod(false);
				}
			}
		}
		return 0;
	}
	
	public int remove(String msg)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("users");
		q.addFilter("email", Query.FilterOperator.EQUAL, msg);
		PreparedQuery pq = datastore.prepare(q);
		
		for(Entity user: pq.asIterable())
		{
			datastore.delete(user.getKey());
		}
		return 0;
	}
	
	private void AddUser(User user)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity users = new Entity("users");
		users.setProperty("Nick", user.getNick());
		users.setProperty("email", user.getAddr());
		if(user.isSnoozing())users.setProperty("Snooze", "true");
		else users.setProperty("Snooze", "false");
		if(user.isMod())users.setProperty("Mod", "true");
		else users.setProperty("Mod", "false");
		datastore.put(users);
		
	}
}
