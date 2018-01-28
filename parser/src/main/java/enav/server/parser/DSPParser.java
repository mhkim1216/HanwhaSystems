/**	
 * Created 01.25.2018.
 * Last Modified 01.25.2018.
 * Main class for parsing has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DSPParser
{
	private static ServerSocket sSocket;
	private static Socket parseSocket;
	private static Socket resourceSocket;
	private static int port = 1216;

	public static void main(String[] args)
	{
		try
		{
			while (true)
			{
				sSocket = new ServerSocket(port);
				parseSocket = sSocket.accept();
				resourceSocket = sSocket.accept();
				
				if(parseSocket!=null && resourceSocket!=null)
					break;
			}
			Thread parseThread = new Thread(new Runnable()
			{

				public void run()
				{
					ParseManager.getManager(parseSocket).start();
				}
			}, "parse_server");
			parseThread.start();

			Thread resourceThread = new Thread(new Runnable()
			{

				public void run()
				{
					ResourceManager.getManager(resourceSocket).start();
				}
			}, "resource_server");
			resourceThread.start();

			parseThread.join();
			resourceThread.join();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sSocket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
