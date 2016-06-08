package com.demon.util;

import java.util.Properties;
import java.net.InetAddress;


/**
 * Created by IntelliJ IDEA. Title:HEX类型主键生成器生成类 User: YeRenBin
 * Mail:Org114@163.com Tel:86-028-81255754 Date: 2006-3-15 Time: 22:56:30 To
 * change this template use File | Settings | File Templates.
 */
public class UUIDHexUtils
{

	private static final int IP;

	private String sep = "";

	static
	{
		int ipadd;
		try
		{
			ipadd = toInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception e)
		{
			ipadd = 0;
		}
		IP = ipadd;
	}

	private static short counter = (short) 0;

	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

	/**
	 * Unique across JVMs on this machine (unless they load this class in the
	 * same quater second - very unlikely)
	 */
	private int getJVM()
	{
		return JVM;
	}

	/**
	 * Unique in a millisecond for this JVM instance (unless there are >
	 * Short.MAX_VALUE instances created in a millisecond)
	 */
	private short getCount()
	{
		synchronized (UUIDHexUtils.class)
		{
			if (counter < 0)
				counter = 0;
			return counter++;
		}
	}

	/**
	 * Unique in a local network
	 */
	private int getIP()
	{
		return IP;
	}

	/**
	 * Unique down to millisecond
	 */
	private short getHiTime()
	{
		return (short) (System.currentTimeMillis() >>> 32);
	}

	private int getLoTime()
	{
		return (int) System.currentTimeMillis();
	}

	private static int toInt(byte[] bytes)
	{
		int result = 0;
		for (int i = 0; i < 4; i++)
		{
			result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
		}
		return result;
	}

	public void configure(Properties params)
	{
		sep = params.getProperty("seperator");
	}

	protected String format(int intval)
	{
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	protected String format(short shortval)
	{
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	public String generate()
	{
		return new StringBuffer(36).append(format(getIP())).append(sep).append(
				format(getJVM())).append(sep).append(format(getHiTime()))
				.append(sep).append(format(getLoTime())).append(sep).append(
						format(getCount())).toString();
	}

	public static String getUUIDHex()
	{
		Properties props = new Properties();
		props.setProperty("seperator", "");
		UUIDHexUtils uh = new UUIDHexUtils();
		uh.configure(props);
		return uh.generate();
	}

	/**
	 * 实例
	 * 
	 * @param arg
	 */
	public static void main(String[] arg)
	{
		System.out.println(UUIDHexUtils.getUUIDHex());
	}

}

