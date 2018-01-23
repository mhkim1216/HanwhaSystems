/**	
 * Created 01.23.2018.
 * Last Modified 01.23.2018.
 * Class for describing client has been built using POJO.
 * 
 * 
 */

package enav.monitor.polling;

import java.util.ArrayList;
import java.util.List;

public class Client
{
	private boolean isRunning;
	private String name;

	private String initTime;
	private String lastTime;
	private String usingTime;
	private static int usingCount;
	private static List<Trace> logTrace;

	public Client(String name, String initTime, String lastTime)
	{
		this.isRunning = false;

		if (name == null)
			this.name = name;
		if (initTime == null)
			this.initTime = initTime;
		if (lastTime == null)
			this.lastTime = lastTime;

		//add usingTime calculation
		
		if (logTrace == null)
			logTrace = new ArrayList<Trace>();
			
		//add each client's trace

	}

	public String getInitTime()
	{
		return initTime;
	}

	public void setInitTime(String initTime)
	{
		this.initTime = initTime;
	}

	public String getLastTime()
	{
		return lastTime;
	}

	public void setLastTime(String lastTime)
	{
		this.lastTime = lastTime;
	}

	public String getUsingTime()
	{
		return usingTime;
	}

	public void setUsingTime(String usingTime)
	{
		this.usingTime = usingTime;
	}

	public int getUsingCount()
	{
		return usingCount;
	}

	public void setUsingCount(int usingCount)
	{
		this.usingCount = usingCount;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}

	public void addTrace(int num, String time, String query, boolean result)
	{
 
		logTrace.add(new Trace(num, time, query, result));
	}
}