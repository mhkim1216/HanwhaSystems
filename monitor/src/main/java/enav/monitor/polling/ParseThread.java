package enav.monitor.polling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import enav.monitor.screen.RemoteUI;

class ParseThread extends Thread
{
	private RemoteUI rmt;
	private DualSocket parseSocket;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	private int refresh;
	private String serverIP;

	ParseThread(String ip, int port, RemoteUI rmt, int refresh)
	{
		super("parse_monitor");
		this.rmt = rmt;
		this.refresh = refresh;

		try
		{
			parseSocket = new DualSocket(ip, port, DualSocket.PARSE);
			bis = new BufferedInputStream(parseSocket.getInputStream());
			bos = new BufferedOutputStream(parseSocket.getOutputStream());
			parseSocket.sendSocketId(bos);
			serverIP=parseSocket.getInetAddress().getHostAddress();
			// System.out.println("Send parse bit");
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
		byte[] jsonString = new byte[20000];
		String jsonResource = null;
		JsonParser parser = new JsonParser();
		JsonObject jsonObject;

		String[] result=new String[16];
		
//		result[0] : String requestor;
//		result[1] : String reqType;
//		result[2] : String service;
//		result[3] : String parameter;
//		result[4] : String sessionId;
//		result[5] : String sessionTime;
//		result[6] : String sql;
//
//		result[7] : String errType;
//		result[8] : String errName;
//		result[9] : String errTime;
//		result[10]: String allLog;
//		result[11]: String tomcat;
//		result[12]: String postgres;
//		result[13]: String spring;
//		result[14]: String restapi;
//		result[15]: String java;
		
		while (true)
		{
			try
			{
				if(!parseSocket.isConnected())
					throw new SocketException();
				
				bis.read(jsonString);
				rmt.setOpStatus("RUN");
				jsonResource = new String(jsonString);
				jsonResource = jsonResource.substring(0, jsonResource.lastIndexOf("}") + 1);

//				System.out.println(jsonResource);

				jsonObject = parser.parse(jsonResource).getAsJsonObject();

				result[0] = jsonObject.get("requestor").getAsString();
				result[1] = jsonObject.get("reqType").getAsString();
				result[2] = jsonObject.get("service").getAsString();
				result[3] = jsonObject.get("parameter").getAsString();
				result[4] = jsonObject.get("sessionId").getAsString();
				result[5] = jsonObject.get("sessionTime").getAsString();
				result[6] = jsonObject.get("sql").getAsString();
				result[7] = jsonObject.get("errType").getAsString();
				result[8] = jsonObject.get("errName").getAsString();
				result[9] = jsonObject.get("errTime").getAsString();
				result[10] = jsonObject.get("allLog").getAsString();
				result[11] = jsonObject.get("tomcat").getAsString();
				result[12] = jsonObject.get("postgres").getAsString();
				result[13] = jsonObject.get("spring").getAsString();
				result[14] = jsonObject.get("restapi").getAsString();
				result[15] = jsonObject.get("java").getAsString();

				rmt.setParsingResult(result);
				
				Thread.sleep(1500);	// time to change status from run to idle
				rmt.setOpStatus("IDLE");

				jsonString = new byte[32768];
			}
			catch (IOException e)
			{
				rmt.setDisconnected();
				e.printStackTrace();
				break;
			}
			catch(InterruptedException e)
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
