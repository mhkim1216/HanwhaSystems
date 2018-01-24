/**	
 * Created 01.23.2018.
 * Last Modified 01.23.2018.
 * Class for describing client has been built using POJO.
 * 
 * 
 */

package enav.monitor.polling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Client
{
	private boolean isRunning;
	private String name;
	private String initTime;
	private String lastTime;
	private String usingTime;
	private int usingCount;
	private static List<Trace> sqlList;

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

		if (sqlList == null)
			sqlList = new ArrayList<Trace>();

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
		usingTime = calculateUsingTime(initTime, lastTime);
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

	public void update(String lastTime)
	{
		this.lastTime = lastTime;
		this.usingTime = calculateUsingTime(initTime, lastTime);
		// ++(this.usingCount);
	}

	private String calculateUsingTime(String init, String last)
	{
		String dateStart = init;
		String dateStop = last;

		// HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date d1 = null;
		Date d2 = null;

		StringBuilder sb = null;
		;

		try
		{
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);

			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);

			sb = new StringBuilder();
			sb.append(diffDays).append("d ").append(diffHours).append(':').append(diffMinutes).append(':')
					.append(diffSeconds);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return sb.toString();
	}
	

	public void addTrace(int num, String initTime, String query, boolean result)
	{

		sqlList.add(new Trace(num, initTime, query, result));
	}

}
