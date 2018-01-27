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
import java.net.Socket;

public class ParseManager
{
	private static ParseManager manager;
	private Parser parser;
	private ServerSocket sSocket;
	private Socket socket;
	private static final int port=1216;
	
	
	private ParseManager()
	{
		
	
	}
	
	public static ParseManager getManager()
	{
		if(manager!=null)
		{	
			System.out.println("ParseManager is created");
			return manager;
		}
		
		
		else
		{	
			manager=new ParseManager();
			System.out.println("ParseManager is created");
			return manager;
		}
	}
	
	public void start()
	{
		System.out.println("Creating session between DSP and DSP monitor...");
		createSession();
	}
	
	private void createSession()
	{
		try
		{
			sSocket=new ServerSocket(port);
			socket=sSocket.accept();
			
			System.out.println("Session is created");
			
			createThreadsAndRun();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	}
	
	private void createThreadsAndRun()
	{
		
	}
}

