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

import enav.monitor.screen.Trace;

public class Client
{
	private boolean isRunning;
	private String name;
	private String initSession;
	private String lastSession;
	private String usingTime;
	private int usingCount;
	private String query;
	private List<Trace> sqlList;

	public Client()
	{
		// nothing to do
	}

	public Client(String name, String initSession, String lastSession, String query)
	{
		this.isRunning = true;

		if (this.name == null)
			this.name = name;
		if (this.initSession == null)
			this.initSession = initSession;
		if (this.lastSession == null)
			this.lastSession = lastSession;

		this.usingTime = getUsingTime();
		this.query=query;

		this.sqlList = new ArrayList<Trace>();

		// add each client's trace

	}

	public String getInitSession()
	{
		return initSession;
	}

	public void setInitSession(String initSession)
	{
		this.initSession = initSession;
	}

	public String getLastSession()
	{
		return lastSession;
	}

	public void setLastSession(String lastSession)
	{
		this.lastSession = lastSession;
	}

	public String getUsingTime()
	{
		usingTime = calculateUsingTime(initSession, lastSession);
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
	
	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public List<Trace> getSqlList()
	{
		return sqlList;
	}

	public void setSqlList(List<Trace> sqlList)
	{
		this.sqlList = sqlList;
	}

	public void setUsingCount(int usingCount)
	{
		this.usingCount = usingCount;
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}

	public void update(String lastSession, String query)
	{
		this.lastSession = lastSession;
		this.query=query;
		this.usingTime = calculateUsingTime(initSession, lastSession);
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
	

	public void addTrace(String firstSession, String lastSession, String query)
	{

		sqlList.add(new Trace(firstSession, lastSession, query));
	}

}
