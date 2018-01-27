package enav.monitor.polling;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

class ParseThread extends Thread
{
	private String ip;
	private int port;
	
	private Socket parseSocket;
	private BufferedInputStream bis;

	ParseThread(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void run()
	{
		try
		{
			Socket parseSocket = new Socket(ip, port);
			bis = new BufferedInputStream(parseSocket.getInputStream());

		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
