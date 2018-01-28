/**	
 * Created 01.25.2018.
 * Last Modified 01.28.2018.
 * Class for managing parser has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ParseManager
{
	private static ParseManager manager;
	private Socket socket;
	private BufferedOutputStream bos;
	private int refresh;
	
	
	private ParseManager(Socket socket)
	{
		refresh=1000;
		this.socket=socket;
	}
	
	public static ParseManager getManager(Socket socket)
	{
		if(manager!=null)
		{	
			System.out.println("ParseManager is created");
			return manager;
		}
		
		
		else
		{	
			manager=new ParseManager(socket);
			System.out.println("ParseManager is created");
			return manager;
		}
	}
	
	public void start()
	{
		System.out.println("Creating stream between DSP and DSP monitor...");
		createStream();
	}
	
	private void createStream()
	{
		try
		{
			bos=new BufferedOutputStream(socket.getOutputStream());
			System.out.println("Resource stream is created");

			while (true)
			{
				createParser();
				Thread.sleep(refresh);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	private void createParser()
	{
		
	}
}

