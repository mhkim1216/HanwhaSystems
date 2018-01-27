/**	
 * Created 01.25.2018.
 * Last Modified 01.25.2018.
 * Main class for parsing has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

public class DSPParser
{

	public static void main(String[] args)
	{
		Thread parseThread = new Thread(new Runnable()
		{

			public void run()
			{
				ParseManager.getManager().start();
			}
		}, "parse_server");
//		parseThread.start();
		
		Thread resourceThread = new Thread(new Runnable()
		{

			public void run()
			{
				ResourceManager.getManager().start();
			}
		}, "resource_server");
		resourceThread.start();
		
		try
		{
//			parseThread.join();
			resourceThread.join();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
