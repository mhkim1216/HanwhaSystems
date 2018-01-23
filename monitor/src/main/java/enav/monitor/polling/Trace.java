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

}