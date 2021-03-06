package enav.monitor.polling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import enav.monitor.screen.RemoteUI;

class ResourceThread extends Thread
{
	private RemoteUI rmt;
	private DualSocket resourceSocket;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	private int refresh;
	private String serverIP;

	ResourceThread(String ip, int port, RemoteUI rmt, int refresh)
	{
		super("resource_monitor");
		this.rmt = rmt;
		this.refresh = refresh;

		try
		{
			resourceSocket = new DualSocket(ip, port, DualSocket.RESOURCE);
			bis = new BufferedInputStream(resourceSocket.getInputStream());
			bos = new BufferedOutputStream(resourceSocket.getOutputStream());
			resourceSocket.sendSocketId(bos);
			serverIP=resourceSocket.getInetAddress().getHostAddress();
//			System.out.println("Send resource bit");
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
		byte[] resource = new byte[1024];
		String jsonResource = null;
		JsonParser parser = new JsonParser();
		JsonObject jsonObject;

		double used;
		double total;

		while (true)
		{
			try
			{
				if(!resourceSocket.isConnected())
					throw new SocketException();
				
				bis.read(resource);

				jsonResource = new String(resource);
				jsonResource = jsonResource.substring(0, jsonResource.lastIndexOf("}") + 1);

//				System.out.println(jsonResource);

				jsonObject = parser.parse(jsonResource).getAsJsonObject();

				used = jsonObject.get("ramUsed").getAsDouble();
				total = jsonObject.get("ramTotal").getAsDouble();
				rmt.setResourceStatus(used, total, "ram");

				used = jsonObject.get("cpuUsed").getAsDouble();
				rmt.setResourceStatus(used, 1, "cpu");

				used = jsonObject.get("diskUsed").getAsDouble();
				total = jsonObject.get("diskTotal").getAsDouble();
				rmt.setResourceStatus(used, total, "disk");

				used = jsonObject.get("netUsed").getAsDouble();
				rmt.setResourceStatus(used, 1000, "net");

				used = jsonObject.get("efficiency").getAsDouble();
				rmt.setResourceStatus(used * 100, 0, "eff");
		
				Thread.sleep(refresh);

				resource = new byte[1024];
			}
			catch (IOException e)
			{
				rmt.setDisconnected();
				e.printStackTrace();
				break;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String getServerIP()
	{
		return serverIP;
	}
}
