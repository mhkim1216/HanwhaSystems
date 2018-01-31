/**	
 * Created 01.30.2018.
 * Last Modified 01.30.2018.
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
	
	public DSPLog()
	{
		
		
		
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
	
	
	
}
