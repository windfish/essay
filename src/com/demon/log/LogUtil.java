package com.demon.log;

import org.slf4j.MDC;

/**
 * 日志工具类
 * 
 */
public class LogUtil {

	private static final String LOG_FILE_NAME = "main_alias";
	
	/**
	 * 标记
	 */
	public static void mark(String fileName){
		MDC.put(LOG_FILE_NAME, fileName);
	}
	
	public static void reset(){
		MDC.remove(LOG_FILE_NAME);
	}
	
}
