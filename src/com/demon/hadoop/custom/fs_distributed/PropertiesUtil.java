package com.demon.hadoop.custom.fs_distributed;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

    private static Properties properties = null;
    private static final String DEFAULT_ENCODING = "UTF-8";

    static {
        properties = new Properties();
        InputStream in = null;
        try {
            File file = new File("src/com/demon/hadoop/custom/fs_distributed/server.properties");
            in = new BufferedInputStream(new FileInputStream(file));
            properties.load(new InputStreamReader(in, DEFAULT_ENCODING));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}
