/**	
 * Created 01.26.2018.
 * Last Modified 01.27.2018.
 * Class for managing resource has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

public class ResourceManager
{
	private static ResourceManager manager;
	private ServerSocket sSocket;
	private Socket socket;
	private static final int port = 1216;
	private BufferedOutputStream bos;
	private int refresh;

	private ResourceManager()
	{
		refresh=1000;
	}

	public static ResourceManager getManager()
	{
		if (manager != null)
		{
			System.out.println("ResourceManager is created");
			return manager;
		}

		else
		{
			manager = new ResourceManager();
			System.out.println("ResourceManager is created");
			return manager;
		}
	}

	public void start()
	{
		System.out.println("Creating session between DSP server and DSP monitor...");
		createSession();
	}

	private void createSession()
	{
		try
		{
			sSocket = new ServerSocket(port);
			socket = sSocket.accept();
			bos=new BufferedOutputStream(socket.getOutputStream());
			
			System.out.println("Session is created");

			while (true)
			{
				createMonitor();
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

	private void createMonitor() throws IOException
	{
		Resource res = new Resource();
		Gson gson =new Gson();
		String resource=gson.toJson(res);
		System.out.println(resource);
		bos.write(resource.getBytes());
		bos.flush();
	}
}
