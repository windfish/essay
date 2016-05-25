package com.demon.util;

public class ExceptionUtils {

	/**
	 * 获取异常的详细信息
	 */
	public static String getExceptionDetail(Exception e) {

		StringBuffer msg = new StringBuffer("");
		if (e != null) {
			String message = e.toString();
			int length = e.getStackTrace().length;
			if (length > 0) {
				msg.append(message + "\n");

				for (int i = 0; i < length; i++) {
					StackTraceElement stackMsg = e.getStackTrace()[i];
					if(stackMsg.toString().indexOf("com.demon") != -1){
						msg.append("\t" + stackMsg + "\n");
					}
				}
			} else {
				msg.append(message);
			}
		}
		return msg.toString();
	}
	
}
