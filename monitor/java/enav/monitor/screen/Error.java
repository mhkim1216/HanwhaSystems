/**	
 * Created 02.17.2018.
 * Last Modified 02.17.2018.
 * Class for describing error has been built using POJO.
 * 
 * 
 */

package enav.monitor.screen;

public class Error
{
	String time;
	String errorType;
	String errorName;
	
	public Error(String time, String errorType, String errorName)
	{
		this.time=time;
		this.errorType=errorType;
		this.errorName=errorName;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getErrorType()
	{
		return errorType;
	}

	public void setErrorType(String errorType)
	{
		this.errorType = errorType;
	}

	public String getErrorName()
	{
		return errorName;
	}

	public void setErrorName(String errorName)
	{
		this.errorName = errorName;
	}

	
}
