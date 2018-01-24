/**	
 * Created 01.23.2018.
 * Last Modified 01.23.2018.
 * Class for describing each trace has been built using POJO.
 * 
 * 
 */

package enav.monitor.polling;

public class Trace
{
	int num;
	String time;
	String query;
	boolean result;

	Trace(int num, String time, String query, boolean result)
	{
		this.num = num;
		this.time = time;
		this.query = query;
		this.result = result;
	}

	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public boolean isResult()
	{
		return result;
	}

	public void setResult(boolean result)
	{
		this.result = result;
	}

}