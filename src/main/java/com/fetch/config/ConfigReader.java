package com.fetch.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading and retrieving configuration properties from a {@code config.properties} file.
 * This class provides methods to fetch static configuration values as well as formatted values with dynamic placeholders.
 */
public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(ConfigReader.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /**
     * Overrides the properties for testing purposes.
     * @param newProperties The test properties to use.
     */
    public static void overrideProperties(Properties newProperties) {
        properties.clear();
        properties.putAll(newProperties);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getFormattedProperty(String key, String... replacements) {
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) {
                throw new IllegalArgumentException("Replacement arguments must be in pairs.");
            }
            value = value.replace(replacements[i], replacements[i + 1]);
        }
        return value;
    }
}
