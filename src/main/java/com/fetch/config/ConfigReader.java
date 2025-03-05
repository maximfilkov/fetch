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
     * Retrieves the value of a specified property key.
     *
     * @param key the property key to retrieve.
     * @return the value of the property if found, otherwise {@code null}.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves a property value and replaces placeholders with specified values.
     * <p>
     * Example usage:
     * <pre>
     *     String url = ConfigReader.getFormattedProperty("api_url",
     *                     "{base_url}", "https://example.com",
     *                     "{api_key}", "123456");
     * </pre>
     *
     * @param key          the property key to retrieve.
     * @param replacements an array of placeholder-value pairs to be replaced within the property value.
     *                     Must be in the format: {@code "{placeholder1}", "value1", "{placeholder2}", "value2", ...}
     * @return the formatted property value with placeholders replaced.
     * @throws IllegalArgumentException if an odd number of replacement arguments is provided.
     */
    public static String getFormattedProperty(String key, String... replacements) {
        String value = properties.getProperty(key);
        if (value == null) {
            return null; // Return null if the property is missing.
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
