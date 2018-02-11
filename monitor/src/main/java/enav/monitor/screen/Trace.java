package enav.monitor.screen;

public class Trace
{
	String firstSession;
	String lastSession;
	String sql;
	
	public Trace(String firstSession, String lastSession, String sql)
	{
		this.firstSession=firstSession;
		this.lastSession=lastSession;
		this.sql=sql;
	}

	public String getFirstSession()
	{
		return firstSession;
	}

	public void setFirstSession(String firstSession)
	{
		this.firstSession = firstSession;
	}

	public String getLastSession()
	{
		return lastSession;
	}

	public void setLastSession(String lastSession)
	{
		this.lastSession = lastSession;
	}

	public String getSql()
	{
		return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}
	
	
}
