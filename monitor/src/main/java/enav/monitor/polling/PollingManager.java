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
import enav.monitor.screen.RemoteUI;

public class PollingManager
{
	private static PollingManager manager;
	
	// managing clients list in history tab.
	private Map<String, Client> clients;


	private RemoteUI rmt;
	private History his;
	private LogTrace log;
	private ErrorTrace err;

	private PollingManager(RemoteUI rmt, History his, LogTrace log, ErrorTrace err)
	{
		this.rmt = rmt;
		this.his = his;
		this.log = log;
		this.err = err;
		this.clients = new HashMap<String, Client>();
	}

	public static PollingManager getInstance(RemoteUI rmt, History his, LogTrace log, ErrorTrace err)
	{
		if (manager == null)
			return (new PollingManager(rmt, his, log, err));
		else
			return manager;
	}

	public void monitor()
	{
		// what manager thread has to do
		// set Client.name, Client.initTime, Client.lastTime...etc.
		Client.initCommon="2018-01-24 10:23:53";
		Client.lastCommon="2018-01-24 11:09:27";
		
		//test codes. Requesting Row-Pane in History tab.
		test("client1");
		test("client1");
		test("client2");
		test("client3");
		test("client4");
		test("client4");
		test("client4");
		test("client1");
		test("client1");
		test("client5");
	}
	
	// if new session is created(manager thread is interrupted)
	public void test(String name)
	{
		his.addClient(getClient(name));
	}

	private Client getClient(String name)
	{
		// at least two times
		if (clients.containsKey(name))
		{
			Client client=clients.get(name);
			client.update(Client.lastCommon);
			return client;
		}
		
		// at first time
		else
		{
			clients.put(name, new Client(name, Client.initCommon, Client.lastCommon));
			return clients.get(name);
		}
	}

}
