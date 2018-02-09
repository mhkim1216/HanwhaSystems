/**	
 * Created 01.25.2018.
 * Last Modified 02.08.2018.
 * Class for describing parser has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

public class Parser
{
	private BufferedOutputStream bos;
	private BufferedReader bis;
	private RandomAccessFile raf;
	private DSPLog dspLog;
	private String rawString;
	private File log;
	private File ver;
	private final static int refresh = 500;

	public Parser(BufferedOutputStream bos)
	{
		this.bos = bos;
		dspLog = new DSPLog();
	}

	public void monitor()
	{
		byte[] rawBytes = new byte[20000];
		long prevFileSize = 0;
		long curFileSize = 0;
		boolean isInit = true;

		try
		{
			while (true)
			{
				Thread.sleep(refresh);

				getVersion();

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
				rawString = new String(rawBytes).trim();

				parse();

				rawBytes = new byte[20000];
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
		sendLine();
		parseInit();
		parseLast();
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
		// System.out.println(parsedJson);
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

	private void sendLine() throws IOException
	{
		dspLog.setAllLog(rawString);
	}

	private void getVersion()
	{
		try
		{
			// tomcat version
			dspLog.setTomcat(System.getenv("CATALINA_HOME").split("-")[2]);
			dspLog.setJava(System.getenv("JAVA_HOME").split("jdk")[1]);

			ver = new File(System.getenv("CATALINA_HOME")
					+ "\\webapps\\rest-api\\META-INF\\maven\\com.hanwha.sample.spring.mybatis\\spring-sample-mybatis\\pom.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(ver);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("dependency");

			for (int i = 0; i < nList.getLength(); ++i)
			{
				if (nList.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) (nList.item(i));
					String app = element.getElementsByTagName("groupId").item(0).getTextContent();

					// postgreSQL version
					if (app.equals("org.postgresql"))
						dspLog.setPostgres(element.getElementsByTagName("version").item(0).getTextContent());
				}
			}

			// spring boot version
			dspLog.setSpring(((Element) (((Element) (doc.getElementsByTagName("parent").item(0)))
					.getElementsByTagName("version").item(0))).getTextContent());

			// restapi version
			dspLog.setRestapi(((Element) (((Element) (doc.getElementsByTagName("project").item(0)))
					.getElementsByTagName("version").item(0))).getTextContent());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// System.out.println(dspLog.getTomcat());
		// System.out.println(dspLog.getPostgres());
		// System.out.println(dspLog.getSpring());
		// System.out.println(dspLog.getRestapi());
		// System.out.println(dspLog.getJava());
	}

	private void parseInit()
	{
		dspLog.setFirstSession(rawString.substring(0, rawString.indexOf(",")));
//		System.out.println("init session : " + dspLog.getFirstSession());
	}

	private void parseLast()
	{
		dspLog.setLastSession(rawString.substring(rawString.lastIndexOf(",") - 19, rawString.lastIndexOf(",")));
//		System.out.println("last session : " + dspLog.getLastSession());
	}

}
