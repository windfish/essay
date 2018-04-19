package com.demon.test;

import java.util.concurrent.TimeUnit;

public class Test {

	public static void main(String[] args) {
	    System.out.println(decodeUnicode("\\u53d1\\u9001\\u4ea4\\u6613\\u62a5\\u4ef7\\u65f6\\u53d1\\u751f\\u4e86\\u4e00\\u4e2a\\u9519\\u8bef\\u3002\\u8bf7\\u7a0d\\u540e\\u518d\\u8bd5\\u3002"));
//	    System.out.println(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
//	    System.out.println(1521806247);
	    
		/*System.out.println("税后new: "+(14500*13+450*12+600*12+600));
		System.out.println("税后old: "+(10500*13+3360*12+2100));
		
		System.out.println("税前new: "+(16000*13+450*12+300*12));
		System.out.println("税前old: "+(14000*13+450*12+1680*12));
		
		//显示获取当前代码的行数
		System.out.println(new Throwable().getStackTrace()[0].getLineNumber());
		
		String s = "aaa";
		test(s);
		System.out.println(s);
		
		int i=0;
		testInt(i);
		System.out.println(i);
		
		StringBuffer sb = new StringBuffer();
		testStringBuffer(sb);
		System.out.println(sb);*/
	}
	
	public static String decodeUnicode(final String dataStr) {     
        int start = 0;     
        int end = 0;     
        final StringBuffer buffer = new StringBuffer();     
        while (start > -1) {     
            end = dataStr.indexOf("\\u", start + 2);     
            String charStr = "";     
            if (end == -1) {     
                charStr = dataStr.substring(start + 2, dataStr.length());     
            } else {     
                charStr = dataStr.substring(start + 2, end);     
            }     
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。     
            buffer.append(new Character(letter).toString());     
            start = end;     
        }     
        return buffer.toString();     
     }
	
	public static void test(String str){
		str = "123";
	}
	
	public static void testInt(int a){
		a=3;
	}
	
	public static void testStringBuffer(StringBuffer sb){
		sb.append("123123");
	}

}
