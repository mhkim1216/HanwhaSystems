/**	
 * Created 01.25.2018.
 * Last Modified 01.25.2018.
 * Class for describing parser has been built using POJO.
 * 
 * 
 */

package enav.server.parser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Parser
{
	private BufferedOutputStream bos;
	private BufferedInputStream bis;
	private long prevPos;
	private File log;
	private final static int refresh=500;

	public Parser(BufferedOutputStream bos)
	{
		this.bos = bos;
		this.prevPos = 0;
	}

	public void monitor()
	{
		byte[] rawBytes = new byte[32768];
		String rawString;
		long prevFileSize=0;
		long curFileSize=0;
		boolean isInit=true;

		try
		{
			while (true)
			{
				Thread.sleep(refresh);
				// need RandomAccessFile class
				log = new File(System.getenv("CATALINA_HOME") + "/eNaviLogs/eNaviService.log");
				bis = new BufferedInputStream(new FileInputStream(log));
				prevFileSize=curFileSize;
				curFileSize=log.length();
	
				if(isInit || prevFileSize==curFileSize)
				{	
					isInit=false;
					continue;
				}
				
				bis.mark((int) prevPos);
				bis.reset();
				bis.read(rawBytes);
				prevPos = curFileSize;
				rawString = new String(rawBytes);
				
				
				System.out.println(rawString);
			
				parse(rawString);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				bis.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void parse(String rawString)
	{

	}

}
