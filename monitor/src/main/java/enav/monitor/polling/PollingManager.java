/**
 * Created 01.23.2018.
 * Last Modified 01.23.2018.
 * Class for managing polling system has been built using POJO.
 * 
 * 
 */

package enav.monitor.polling;

import java.util.HashMap;
import java.util.Map;

import enav.monitor.screen.ErrorTrace;
import enav.monitor.screen.History;
import enav.monitor.screen.LogTrace;

public class PollingManager
{
	private static PollingManager manager;
	private Map<String, Client>client;
	
	private History his;
	private LogTrace log;
	private ErrorTrace err;
	
	private PollingManager(History his, LogTrace log, ErrorTrace err)
	{
		this.his=his;
		this.log=log;
		this.err=err;
		this.client=new HashMap<String, Client>();
	}
	
	public static PollingManager getInstance(History his, LogTrace log, ErrorTrace err)
	{
		if(manager==null)
			return (new PollingManager(his, log, err));
		else
			return manager;
	}
	
	//if new session is created(manager thread is interrupted)
	public void test(String name)
	{
		his.addClient(getClient(name));
	}
	
	private Client getClient(String name)
	{
		if(client.containsKey(name))
			return client.get(name);
		else
		{
			client.put(name, new Client(name, "init_time", "last_time"));
			return client.get(name);
		}
	}
	
	
}
