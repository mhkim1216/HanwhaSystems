/**	
 * Created 01.25.2018.
 * Last Modified 01.25.2018.
 * Class for describing parser has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.google.gson.Gson;

public class Parser
{
	private BufferedOutputStream bos;
	private RandomAccessFile raf;
	private DSPLog dspLog;
	private String rawString;
	private File log;
	private final static int refresh=500;

	public Parser(BufferedOutputStream bos)
	{
		this.bos = bos;
		dspLog=new DSPLog();
	}

	public void monitor()
	{
		byte[] rawBytes = new byte[32768];
		long prevFileSize=0;
		long curFileSize=0;
		boolean isInit=true;

		try
		{
			while (true)
			{
				Thread.sleep(refresh);
				
				log = new File(System.getenv("CATALINA_HOME") + "/eNaviLogs/eNaviService.log");
				raf = new RandomAccessFile(log, "r");
				prevFileSize=curFileSize;
				curFileSize=log.length();
	
				if(isInit || prevFileSize==curFileSize)
				{	
					isInit=false;
					continue;
				}
				raf.seek(prevFileSize);
				raf.read(rawBytes);
				rawString = new String(rawBytes);
			
				parse();
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
				raf.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void parse() throws IOException
	{
		parseRequestor();
		parseRequestType();
		parseService();
		parseParam();
		parseSessionId();
		parseSessionTime();
		parseSQL();
		
		Gson gson =new Gson();
		String parsedJson=gson.toJson(dspLog);
		System.out.println(parsedJson);
		bos.write(parsedJson.getBytes());
		bos.flush();
	}
	
	private void parseRequestor()
	{
		
		
	}
	
	private void parseRequestType()
	{
		
		
	}
	
	private void parseService()
	{
		
		
	}
	
	private void parseParam()
	{
		
		
	}
	
	private void parseSessionId()
	{
		
		
	}
	
	private void parseSessionTime()
	{
		
		
	}
	
	private void parseSQL()
	{
		dspLog.setSql(rawString.split("==>  Preparing: ")[1].split("\n")[0]);
//		System.out.println(dspLog.getSql());
	}

}
