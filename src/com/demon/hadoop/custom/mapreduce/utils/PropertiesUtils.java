package com.demon.hadoop.custom.mapreduce.utils;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtils {

    public static void main(String[] args) {
        Properties properties = loadConfig();
        System.out.println(properties.getProperty("mapreduce_temp"));
    }

    public static Properties loadConfig(){
        String default_encoding = "UTF-8";
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File("src/com/demon/hadoop/custom/mapreduce/source.properties")));
            properties.load(new InputStreamReader(in, default_encoding));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
