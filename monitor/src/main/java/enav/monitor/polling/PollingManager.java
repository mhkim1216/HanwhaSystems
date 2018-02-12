/**
 * Created 01.23.2018.
 * Last Modified 01.27.2018.
 * Class for managing polling system has been built using POJO.
 * 
 * 
 */

package enav.monitor.polling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enav.monitor.screen.ErrorTrace;
import enav.monitor.screen.History;
import enav.monitor.screen.LogTrace;
import enav.monitor.screen.RemoteUI;
import enav.monitor.screen.Trace;

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
		{	
			manager=new PollingManager(rmt, his, log, err);
			return manager;
		}
		else
			return manager;
	}
	
	public static PollingManager getInstance()
	{
		return manager;
	}

	public void monitor(String ip, int port)
	{
		ParseThread pThread=new ParseThread(ip, port, rmt, 1000);
		pThread.start();
		ResourceThread rThread=new ResourceThread(ip, port, rmt, 1000);
		rThread.start();
		
		String pServerIP=pThread.getServerIP();
		String rServerIP=rThread.getServerIP();
		
		if(pServerIP.equals(rServerIP))
			if(pServerIP!=null && rServerIP!=null)
				rmt.setConnected(pServerIP);
		
		// test codes. Requesting Row-Pane in History tab.

		// empty
		
		
	}



	// if new session is created(manager thread is interrupted)
	public void updateHistory(String name, String firstSession, String lastSession, String query)
	{
//		System.out.println(initSession);
//		System.out.println(lastSession);
		
		Client client;
		
		// at least two times
		if (clients.containsKey(name))
		{
			client = clients.get(name);
			client.update(lastSession, query);
		}

		// at first time
		else
		{
			client=new Client(name, firstSession, lastSession, query);
			clients.put(name, client);
		}
		
		// add or update each client's all logs(=trace).
		client.addTrace(firstSession, lastSession, query);
		
		his.addClient(client);
	}
	
	public Map<String, Client> getClientList()
	{
		return clients;
	}
}
