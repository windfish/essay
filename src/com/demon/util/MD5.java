package com.demon.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	private static MD5 instance = new MD5();
	
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private MessageDigest mdInstance = null;

    private MD5() {
        try {
            mdInstance = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public static MD5 getInstance() {
    	return instance;
    }
    
    public synchronized String encode(final String origin) {
        return encode(origin, "utf-8");
    }
    
    public synchronized String encode(final String origin, String charset) {
    	String r = "";
    	try {
    		if (charset == null || charset.length() == 0) {
    			r = byteArrayToHexString(mdInstance.digest(origin.getBytes()));
			} else {
		        r = byteArrayToHexString(mdInstance.digest(origin.getBytes(charset)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return r;
    }

    public synchronized String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + ":";
            }
        }
        return hs.toUpperCase();
    }
    
    private String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    
    public static void main(String[] args) {
		System.out.println(MD5.getInstance().encode((Math.random()+"").toString()));
	}
}
