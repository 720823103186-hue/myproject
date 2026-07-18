package com.employee.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try {
            String path = "src/test/resources/config.properties";
            FileInputStream input = new FileInputStream(path);
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            System.out.println("config.properties not found, using default settings.");
            properties = new Properties();
            properties.setProperty("browser", "chrome");
            properties.setProperty("url", "http://localhost:5174/");
            properties.setProperty("implicit.wait", "10");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
