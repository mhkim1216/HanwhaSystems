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
	private int usingCount;
	private static List<Trace> logTrace;

	public static String initCommon;
	public static String lastCommon;

	public Client()
	{
		// nothing to do
	}

	public Client(String name, String initTime, String lastTime)
	{
		this.isRunning = true;

		if (this.name == null)
			this.name = name;
		if (this.initTime == null)
			this.initTime = initTime;
		if (this.lastTime == null)
			this.lastTime = lastTime;

		this.usingTime = getUsingTime();

		if (logTrace == null)
			logTrace = new ArrayList<Trace>();

		// add each client's trace

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
		usingTime=calculateUsingTime(initTime, lastTime);
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

	public void increaseUsingCount()
	{
		++usingCount;
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

	public void update(String lastTime)
	{
		this.lastTime = lastTime;
		this.usingTime = calculateUsingTime(initTime, lastTime);
		// ++(this.usingCount);
	}

	private String calculateUsingTime(String init, String last)
	{
		// make algorithm to calculate usingTime
		return "temp_time";
	}
}
