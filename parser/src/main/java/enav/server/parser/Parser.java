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
	private final static int refresh = 500;

	public Parser(BufferedOutputStream bos)
	{
		this.bos = bos;
		dspLog = new DSPLog();
	}

	public void monitor()
	{
		byte[] rawBytes = new byte[32768];
		long prevFileSize = 0;
		long curFileSize = 0;
		boolean isInit = true;

		try
		{
			while (true)
			{
				Thread.sleep(refresh);

				log = new File(System.getenv("CATALINA_HOME") + "/eNaviLogs/eNaviService.log");
				raf = new RandomAccessFile(log, "r");
				prevFileSize = curFileSize;
				curFileSize = log.length();

				if (isInit || prevFileSize == curFileSize)
				{
					isInit = false;
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
		parseException();
		// parseError();

		Gson gson = new Gson();
		String parsedJson = gson.toJson(dspLog);
		System.out.println(parsedJson);
		bos.write(parsedJson.getBytes());
		bos.flush();
	}

	private void parseRequestor()
	{
		try
		{
			dspLog.setRequestor(rawString.split("RemoteIpAddress: ")[1].split("; SessionId: ")[0]);
			// System.out.println(dspLog.getRequestor());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			dspLog.setRequestor("Unknown SV");
		}
	}

	private void parseRequestType()
	{
		try
		{
			dspLog.setReqType(rawString.split("'dispatcherServlet' processing ")[1].split(" request for ")[0].trim());
			// System.out.println(dspLog.getReqType());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			dspLog.setReqType("Unknown Type");
		}
	}

	private void parseService()
	{
		try
		{
			String partialString = rawString.split("  - <==")[0];
			int lastIndex = partialString.lastIndexOf(".");
			dspLog.setService(partialString.substring(lastIndex + 1).trim());
			// System.out.println(dspLog.getService());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			dspLog.setService("Unknown Service");
		}
	}

	private void parseParam()
	{
		try
		{
			String partialString = rawString.split(" at position ")[0];

			// GET type
			if (partialString.contains("?"))
			{
				int lastIndex;
				lastIndex = partialString.lastIndexOf("?");
				dspLog.setParameter(partialString.substring(lastIndex + 1));
			}

			// POST type
			else
			{
				dspLog.setParameter(rawString.split("==> Parameters: ")[1].split("\n")[0]);
			}

			// System.out.println(dspLog.getParameter());
		}
		catch (Exception e)
		{
			dspLog.setParameter("No Parameter");
		}
	}

	private void parseSessionId()
	{
		try
		{
			dspLog.setSessionId(rawString.split("; SessionId: ")[1].split("; Granted Authorities")[0]);
			// System.out.println(dspLog.getSessionId());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			dspLog.setSessionId("Unknown ID");
		}
	}

	private void parseSessionTime()
	{
		try
		{
			dspLog.setSessionTime(rawString.substring(0, 19));
			// System.out.println(dspLog.getSessionTime());
		}
		catch (Exception e)
		{
			dspLog.setSessionTime("Unknown Time");
		}
	}

	private void parseSQL()
	{
		try
		{
			dspLog.setSql(rawString.split("==>  Preparing: ")[1].split("\n")[0]);
			// System.out.println(dspLog.getSql());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			dspLog.setSql("No SQL in Request");
		}

	}

	private void parseException()
	{
		if (rawString.contains("Exception: "))
		{
			try
			{
				String partialString = rawString.split("Exception: ")[0];

				int lastIndex;
				lastIndex = partialString.lastIndexOf(".");
				dspLog.setErrName(partialString.substring(lastIndex + 1) + "Exception");
				dspLog.setErrType("Exception");
				// System.out.println(dspLog.getParameter());
			}
			catch (Exception e)
			{
				dspLog.setErrName("Unidentified");
				dspLog.setErrType("Unidentified");
			}
		}
		else
		{	
			dspLog.setErrName("No Error");
			dspLog.setErrType("No Error");
			dspLog.setErrTime("No Error");
		}
	}

	private void parseError()
	{
		// To Do
	}

}
