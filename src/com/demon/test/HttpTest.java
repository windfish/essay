package com.demon.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 查找UCanUup 出现了几次
 * @author fish
 * @version 2016年7月21日 下午5:23:06
 */
public class HttpTest {
	private static String urlString = "http://106.75.28.160/UCloud.txt#rd?n";
	private static String str = "UCanUup";

	public static void main(String[] args) throws Exception {
		int total = 0; //次数
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = null;
		while((line=reader.readLine())!=null){
			System.out.println(line);
			String[] split = line.split(str, -1);
			total += (split.length-1);
		}
		System.out.println("#我的代码可以约主播# “UCanUup”出现了"+total+"次");
		//#我的代码可以约主播# “UCanUup”出现了728次
	}

}
