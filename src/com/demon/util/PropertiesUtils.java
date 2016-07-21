package com.demon.util;

import java.io.IOException;
import java.util.Properties;

public enum PropertiesUtils {
	PROPERTIES()
	;
	
	private PropertiesUtils() {
	}
	
	private Properties properties = new Properties();
	
	public Properties getProperties(String path){
		try {
			properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return properties;
	}
	
}
