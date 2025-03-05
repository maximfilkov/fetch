package com.fetch.config;

import org.junit.jupiter.api.*;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

class ConfigReaderTest {

    @BeforeAll
    static void setup() {
        Properties testProperties = new Properties();
        testProperties.setProperty("test.key", "testValue");
        testProperties.setProperty("url.template", "https://api.example.com/data?key={api_key}");
        ConfigReader.overrideProperties(testProperties);
    }

    @Test
    @DisplayName("Should retrieve a valid property value")
    void testGetProperty_ValidKey() {
        String value = ConfigReader.getProperty("test.key");
        assertEquals("testValue", value, "Expected property value does not match.");
    }

    @Test
    @DisplayName("Should return null for missing property")
    void testGetProperty_MissingKey() {
        assertNull(ConfigReader.getProperty("non.existent.key"), "Expected null for non-existent key.");
    }

    @Test
    @DisplayName("Should replace placeholders correctly in formatted property")
    void testGetFormattedProperty_ValidReplacements() {
        String formatted = ConfigReader.getFormattedProperty("url.template", "{api_key}", "12345");
        assertEquals("https://api.example.com/data?key=12345", formatted, "Placeholder replacement failed.");
    }

    @Test
    @DisplayName("Should return null for missing key in formatted property")
    void testGetFormattedProperty_MissingKey() {
        assertNull(ConfigReader.getFormattedProperty("missing.template", "{placeholder}", "value"), "Expected null for missing key.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for odd number of replacements")
    void testGetFormattedProperty_InvalidReplacements() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> ConfigReader.getFormattedProperty("url.template", "{api_key}"),
                "Expected exception for odd number of replacements.");

        assertEquals("Replacement arguments must be in pairs.", thrown.getMessage());
    }
}
