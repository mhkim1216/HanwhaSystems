package enav.monitor.polling;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class DualSocket extends Socket
{
	public final static int PARSE=1;
	public final static int RESOURCE=2;
	
	DualSocket(String ip, int port, int type) throws UnknownHostException, IOException
	{
		super(ip, port);
	}
}