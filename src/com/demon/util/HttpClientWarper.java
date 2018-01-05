package com.demon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientWarper {

	private static Logger logger = LoggerFactory.getLogger(HttpClientWarper.class);

	private static String DEFAULT_CHARSET = "UTF-8";
	
	public static String post(String url, Map<String, String> param) {
		logger.info("post2:" + url);
		return sendPost(url, param, DEFAULT_CHARSET);
	}
	
	public static String get(String url) {
		logger.info("get2:" + url);
		return sendGet(url, DEFAULT_CHARSET);
	}
	
	/**
	 * 使用Get方式获取数据
	 * 
	 * @param url
	 *            URL包括参数，http://HOST/XX?XX=XX&XXX=XXX
	 * @param charset
	 * @return
	 */
	public static String sendGet(String url, String charset) {
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(5000);
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			logger.warn("request url exception, url:" + url, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				logger.error("close inputstream exception:", e2);
			}
		}
		return result.toString();
	}

	/**
	 * POST请求，字符串形式数据
	 * 
	 * @param url 请求地址
	 * @param param 请求数据
	 * @param charset 编码方式
	 */
	public static String sendPostUrl(String url, String param, String charset) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(5000);
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			long contentLength = conn.getContentLengthLong();
			logger.info("sendPostUrl contentLength:{}", contentLength);
			if (contentLength > MAX_RESPONSE || contentLength <= 0){
				logger.error("sendPostUrl response too big, url:{}, param:{}", url, param);
				return null;
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			logger.error("sendPostUrl " + e, e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("sendPostUrl close exception, url:" + url + ", param:" + param, ex);
			}
		}
		return result.toString();
	}
	
	private static final int MAX_RESPONSE = 1024 * 1024;

	/**
	 * POST请求，Map形式数据
	 * 
	 * @param url
	 *            请求地址
	 * @param param
	 *            请求数据
	 * @param charset
	 *            编码方式
	 */
	public static String sendPost(String url, Map<String, String> param, String charset) {
		StringBuffer buffer = new StringBuffer();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
			    if (buffer.length() > 0){
			       buffer.append("&");
			    }
				try {
                    buffer.append(entry.getKey())
                          .append("=")
                          .append(URLEncoder.encode(entry.getValue(), charset));
                } catch (UnsupportedEncodingException e) {
                    logger.error("UnsupportedEncodingException url:" + url + ", param:" + param, e);
                }
			}
		}
		
		URLConnection conn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = realUrl.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(5000);
			
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(buffer);
			// flush输出流的缓冲
			out.flush();
			long contentLength = conn.getContentLengthLong();
			logger.info("sendPost contentLength:{}", contentLength);
			if (contentLength > MAX_RESPONSE || contentLength <= 0){
				logger.info("sendPost response too big, url:{}, param:{}", url, param);
//				return null;
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			logger.error("发送 POST 请求出现异常！" + e, e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("sendPost close exception, url:" + url + ", param:" + param, ex);
			}
		}
		return result.toString();
	}

}
