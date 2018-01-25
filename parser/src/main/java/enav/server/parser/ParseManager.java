/**	
 * Created 01.25.2018.
 * Last Modified 01.25.2018.
 * Class for managing parser has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.IOException;
import java.net.ServerSocket;

public class ParseManager
{
	private static ParseManager manager;
	private Parser parser;
	private ServerSocket sSocket;
	
	private ParseManager()
	{
		
	
	}
	
	public static ParseManager getManager()
	{
		if(manager!=null)
			return manager;
		
		else
		{	
			manager=new ParseManager();
			return manager;
		}
	}
	
	public void start()
	{
		createSession();
	}
	
	private void createSession()
	{
		try
		{
			sSocket=new ServerSocket(1216);
			sSocket.accept();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	}
}
