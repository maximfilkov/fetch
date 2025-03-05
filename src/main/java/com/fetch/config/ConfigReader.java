package com.fetch.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading and retrieving configuration properties from a {@code config.properties} file.
 * <p>
 * Supports:
 * - Reading values from a `config.properties` file.
 * - Overriding values from environment variables.
 * - Fetching and formatting property values with dynamic placeholders.
 */
public class ConfigReader {
    static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * Loads configuration from `config.properties`.
     */
    private static void loadProperties() {
        try {
            // Load properties from config.properties (classpath)
            properties.load(ConfigReader.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /**
     * Retrieves the value of a property key.
     * <p>
     * Checks for an environment variable override before falling back to `config.properties`.
     *
     * @param key The property key to retrieve.
     * @return The value of the property if found, otherwise `null`.
     */
    public static String getProperty(String key) {
        // Check if an environment variable is set (replace dots with underscores to match common env variable naming)
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        return (envValue != null) ? envValue : properties.getProperty(key);
    }

    /**
     * Retrieves a formatted property value with placeholders replaced by dynamic values.
     * <p>
     * Example:
     * <pre>
     * String url = ConfigReader.getFormattedProperty("api_url",
     *                    "{base_url}", "https://example.com",
     *                    "{api_key}", "123456");
     * </pre>
     *
     * @param key          The property key to retrieve.
     * @param replacements An array of placeholder-value pairs.
     *                     Example: `{base_url}, "https://example.com", {api_key}, "123456"`
     * @return The formatted property value with placeholders replaced, or `null` if the key is missing.
     */
    public static String getFormattedProperty(String key, String... replacements) {
        String value = getProperty(key);
        if (value == null) {
            return null; // Return null if the property is missing
        }

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) {
                throw new IllegalArgumentException("Replacement arguments must be in pairs.");
            }
            value = value.replace(replacements[i], replacements[i + 1]);
        }
        return value;
    }

    /**
     * Overrides the properties (for testing purposes).
     * This allows test cases to inject their own values.
     *
     * @param newProperties The test properties to use.
     */
    public static void overrideProperties(Properties newProperties) {
        properties.clear();
        properties.putAll(newProperties);
    }
}
