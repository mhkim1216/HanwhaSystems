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
import java.net.Socket;

import com.google.gson.Gson;

public class ResourceManager
{
	private static ResourceManager manager;
	private Socket socket;
	private BufferedOutputStream bos;
	private int refresh;

	private ResourceManager(Socket socket)
	{
		this.socket=socket;
		refresh=1000;
	}

	public static ResourceManager getManager(Socket socket)
	{
		if (manager != null)
		{
			System.out.println("ResourceManager is created");
			return manager;
		}

		else
		{
			manager = new ResourceManager(socket);
			System.out.println("ResourceManager is created");
			return manager;
		}
	}

	public void start()
	{
		System.out.println("Creating resource stream between DSP server and DSP monitor...");
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
	}

	private void createMonitor() throws IOException
	{
		Resource res = new Resource();
		Gson gson =new Gson();
		String resource=gson.toJson(res);
//		System.out.println(resource);
		bos.write(resource.getBytes());
		bos.flush();
	}
}
