package com.fetch.config;

import org.junit.jupiter.api.*;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Ensures each test gets a fresh instance
class ConfigReaderTest {

    private static final Properties originalProperties = new Properties();

    @BeforeAll
    void backupOriginalProperties() {
        // Store a copy of the current properties to restore later
        originalProperties.putAll(getCurrentProperties());
    }

    @BeforeEach
    void setup() {
        Properties testProperties = new Properties();
        testProperties.setProperty("test.key", "testValue");
        testProperties.setProperty("url.template", "https://api.example.com/data?key={api_key}");
        ConfigReader.overrideProperties(testProperties);
    }

    @AfterEach
    void restoreOriginalProperties() {
        ConfigReader.overrideProperties(originalProperties);
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
        // Ensure property exists before formatting test
        assertNotNull(ConfigReader.getProperty("url.template"), "url.template should not be null before testing formatting.");

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

    @Test
    @DisplayName("Should clear and override properties correctly")
    void testOverrideProperties_ClearsExistingValues() {
        Properties newProperties = new Properties();
        newProperties.setProperty("new.key", "newValue");
        ConfigReader.overrideProperties(newProperties);

        assertNull(ConfigReader.getProperty("test.key"), "Old properties should be cleared.");
        assertEquals("newValue", ConfigReader.getProperty("new.key"), "New property should be available.");
    }

    /**
     * Retrieves a copy of the current properties in `ConfigReader`.
     * This helps in restoring original values after tests.
     *
     * @return A copy of current properties.
     */
    private static Properties getCurrentProperties() {
        Properties copy = new Properties();
        for (String key : ConfigReader.properties.stringPropertyNames()) {
            copy.setProperty(key, ConfigReader.properties.getProperty(key));
        }
        return copy;
    }
}
