package com.szkingdom;

import org.apache.log4j.Logger;


public class LogUtils
{
	private static Logger utilLogger = Logger.getLogger(LogUtils.class);
	
	/**
	 * Ѱ����Ҫ��ӡ��־��LOGGER
	 * @return
	 */
	private static Logger getParentLogger ()
	{
		try
		{
			throw new Exception();
		}
		catch (Exception e)
		{
			StackTraceElement stes[] = e.getStackTrace();
			
			for (int i = 0; i < stes.length; i++)
			{
				try
				{
					if (!Class.forName(stes[i].getClassName()).equals(LogUtils.class))
					{
						return Logger.getLogger(Class.forName(stes[i].getClassName()));
					}
				}
				catch (ClassNotFoundException e1)
				{
					;
				}
			}
		}
		
		return utilLogger;
	}
	
	public static void info(Object message)
	{
		(getParentLogger()).info(message);
	}
	
	public static void info(Class cla, Object message)
	{
		Logger logger = Logger.getLogger(cla);
		logger.info(message);
	}
	
	public static void warn(Object message)
	{
		(getParentLogger()).warn(message);
	}
	
	public static void warn(Class cla, Object message)
	{
		Logger logger = Logger.getLogger(cla);
		logger.warn(message);
	}
	
	public static void debug(Object message)
	{
		(getParentLogger()).debug(message);
	}
	
	public static void debug(int message)
	{
		(getParentLogger()).debug(Integer.toString(message));
	}
	
	public static void debug(Class cla, Object message)
	{
		Logger logger = Logger.getLogger(cla);
		logger.debug(message);
	}
	
	public static void error(Object message)
	{
		(getParentLogger()).error(message);
	}
	
	public static void error(Class cla, Object message)
	{
		Logger logger = Logger.getLogger(cla);
		logger.error(message);
	}
	
	public static void fatal(Object message)
	{
		(getParentLogger()).fatal(message);
	}
	
	public static void fatal(Class cla, Object message)
	{
		Logger logger = Logger.getLogger(cla);
		logger.fatal(message);
	}
	
	/**
	 * ��������־�н��쳣�Ķ�ջ��Ϣ��ӡ����
	 * @param logger
	 * @param stackTraces
	 */
	public static void debugStackTrace(Logger logger, StackTraceElement[] stackTraces)
	{
		for (int i = 0; i < stackTraces.length; i++)
		{
			logger.error(stackTraces[i].toString());
		}
	}
	
	/**
	 * ��������־�н��쳣�Ķ�ջ��Ϣ��ӡ����
	 * @param logger
	 * @param stackTraces
	 */
	public static void debugStackTrace(StackTraceElement[] stackTraces)
	{
		for (int i = 0; i < stackTraces.length; i++)
		{
			utilLogger.error(stackTraces[i].toString());
		}
	}
	
	/**
	 * ����errorCode��ӡ�����Ӧ�Ĵ�����
	 * @param errorCode
	 */
	public static void debugSqlErrorCodeMeaning(int errorCode)
	{
		LogUtils.error(GlobalDBErrorMapping.S_DBERRORMAPPINGINSTANCE.getErrorMeaningByErrorCode(errorCode));
	}
}
