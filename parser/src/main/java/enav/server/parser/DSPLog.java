/**	
 * Created 01.30.2018.
 * Last Modified 02.08.2018.
 * Main class for describing Log itself has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

public class DSPLog
{
	private String requestor="";
	private String reqType="";
	private String service="";
	private String parameter="";
	private String sessionId="";
	private String sessionTime="";
	private String sql="";
	
	private String errType="";
	private String errName="";
	private String errTime="";
	private String allLog="";
	
	private String tomcat="";
	private String postgres="";
	private String spring="";
	private String restapi="";
	private String java="";
	private String javaClient="";
	private String dllClient="";
	
	private String firstSession="";
	private String lastSession="";

	public DSPLog()
	{
		
		
		
	}
	
	public String getTomcat()
	{
		return tomcat;
	}

	public void setTomcat(String tomcat)
	{
		this.tomcat = tomcat;
	}

	public String getPostgres()
	{
		return postgres;
	}

	public void setPostgres(String postgres)
	{
		this.postgres = postgres;
	}

	public String getSpring()
	{
		return spring;
	}

	public void setSpring(String spring)
	{
		this.spring = spring;
	}

	public String getRestapi()
	{
		return restapi;
	}

	public void setRestapi(String restapi)
	{
		this.restapi = restapi;
	}

	public String getJava()
	{
		return java;
	}

	public void setJava(String java)
	{
		this.java = java;
	}

	public String getJavaClient()
	{
		return javaClient;
	}

	public void setJavaClient(String javaClient)
	{
		this.javaClient = javaClient;
	}

	public String getDllClient()
	{
		return dllClient;
	}

	public void setDllClient(String dllClient)
	{
		this.dllClient = dllClient;
	}

	
	public String getAllLog()
	{
		return allLog;
	}

	public void setAllLog(String allLog)
	{
		this.allLog = allLog;
	}
	
	public String getRequestor()
	{
		return requestor;
	}

	public void setRequestor(String requestor)
	{
		this.requestor = requestor;
	}

	public String getReqType()
	{
		return reqType;
	}

	public void setReqType(String reqType)
	{
		this.reqType = reqType;
	}

	public String getService()
	{
		return service;
	}

	public void setService(String service)
	{
		this.service = service;
	}

	public String getParameter()
	{
		return parameter;
	}

	public void setParameter(String parameter)
	{
		this.parameter = parameter;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getSessionTime()
	{
		return sessionTime;
	}

	public void setSessionTime(String sessionTime)
	{
		this.sessionTime = sessionTime;
	}

	public String getSql()
	{
		return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}

	public String getErrType()
	{
		return errType;
	}

	public void setErrType(String errType)
	{
		this.errType = errType;
	}

	public String getErrName()
	{
		return errName;
	}

	public void setErrName(String errName)
	{
		this.errName = errName;
	}

	public String getErrTime()
	{
		return errTime;
	}

	public void setErrTime(String errTime)
	{
		this.errTime = errTime;
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
	
	
	
	
	
}
