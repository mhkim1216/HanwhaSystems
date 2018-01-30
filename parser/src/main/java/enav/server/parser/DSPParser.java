/**	
 * Created 01.25.2018.
 * Last Modified 01.25.2018.
 * Main class for parsing has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DSPParser
{
	private static ServerSocket sSocket;
	private static Socket parseSocket=null;
	private static Socket resourceSocket=null;
	private static int port = 1216;
	public final static int PARSE = 1;
	public final static int RESOURCE = 2;

	public static void main(String[] args)
	{
		try
		{
			InputStream is;
			Socket socket;
			ServerSocket sSocket= new ServerSocket(port);
			int socketId;

			
			while (true)
			{				
				System.out.println("Waiting monitor to connect...");
				
				socket = sSocket.accept();
				
				System.out.println("Checking sockets...");
				
				is = socket.getInputStream();
				socketId=is.read();
				
				if (socketId == DSPParser.PARSE)
				{	
					parseSocket = socket;
					System.out.println("Parser received parse bit");
				}
				else if (socketId == DSPParser.RESOURCE)
				{	
					resourceSocket = socket;
					System.out.println("Parser received resource bit");
				}

				if (resourceSocket != null && parseSocket != null)
					break;
			}
			
			System.out.println("Completed checking sockets...");

			// Call Working Thread
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
