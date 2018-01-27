package enav.server.parser;

import java.io.Serializable;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

class Resource implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double ramTotal;
	private double ramUsed;
	private double cpuTotal;
	private double cpuUsed;
	private double diskTotal;
	private double diskUsed;
	private double netTotal;
	private double netUsed;
	private double efficiency;

	Resource()
	{
		System.out.println("");
		System.out.println("---------------------------------");
		ramTotal = getRamStatus("total");
		ramUsed = getRamStatus("used");
		cpuTotal = 1;
		cpuUsed = getCpuStatus();
		diskTotal = getDiskStatus("total");
		diskUsed = getDiskStatus("used");
		netTotal = 1000;
		netUsed = getAllBounds();
		efficiency = getEfficiency();
		System.out.println("---------------------------------");
		System.out.println("");
	}

	private long getRamStatus(String type)
	{
		long result = 0;

		Sigar sigar = new Sigar();
		Mem mem = null;
		try
		{
			mem = sigar.getMem();
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (type.equals("total"))
		{
			result = mem.getTotal();
			System.out.println("- Ram Total : "+result);
		}

		else if (type.equals("used"))
		{
			result = mem.getActualUsed();
			System.out.println("- Ram Used : "+result);
		}

		else
			result = 0l;

		return result;
	}

	private double getCpuStatus()
	{
		Sigar sigar = new Sigar();
		CpuPerc cpu = null;
		double result;

		try
		{
			cpu = sigar.getCpuPerc();
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			result = cpu.getUser();
			System.out.println("- CPU Load : "+result);
		}

		return result;
	}

	private long getDiskStatus(String type)
	{
		Sigar sigar = new Sigar();
		FileSystemUsage disk = null;
		long result;

		try
		{
			disk = sigar.getFileSystemUsage("C:\\");
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (type.equals("total"))
		{	
			result = disk.getTotal();
			System.out.println("- Disk Total : "+result);
		}

		else if (type.equals("used"))
		{
			result = disk.getUsed();
			System.out.println("- Disk Used : "+result);
		}
		else
			result = 0l;

		return result;
	}

	private int getAllBounds()
	{
		Sigar sigar = new Sigar();
		int result = 0;

		try
		{
			result = sigar.getNetStat().getTcpOutboundTotal() + sigar.getNetStat().getTcpInboundTotal();
			System.out.println("- All Network Bounds : "+result);
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private double getEfficiency()
	{
		efficiency = (ramUsed / ramTotal + cpuUsed / cpuTotal + diskUsed / diskTotal + netUsed / netTotal) / 4;

		return efficiency;
	}
}