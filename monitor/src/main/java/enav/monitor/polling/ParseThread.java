package enav.monitor.polling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import enav.monitor.screen.RemoteUI;

class ParseThread extends Thread
{
	private RemoteUI rmt;
	private DualSocket parseSocket;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	private int refresh;

	ParseThread(String ip, int port, RemoteUI rmt, int refresh)
	{
		super("parse_monitor");
		this.rmt = rmt;
		this.refresh=refresh;
		
		try
		{
			parseSocket = new DualSocket(ip, port, DualSocket.PARSE);
			bis = new BufferedInputStream(parseSocket.getInputStream());
			bos=new BufferedOutputStream(parseSocket.getOutputStream());
			parseSocket.sendSocketId(bos);
//			System.out.println("Send parse bit");
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

	@Override
	public void run()
	{

		
		
		
		
	}
}
