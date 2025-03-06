package com.fetch;

import com.fetch.exceptions.GeolocationException;
import com.fetch.service.GeolocationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class GeolocationCLITest {

    @Mock
    private GeolocationService mockGeolocationService;

    private CommandLine commandLine;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() throws Exception {
        // Redirect stdout and stderr to capture output for testing
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        // Inject the mock service into CLI using reflection
        GeolocationCLI cli = new GeolocationCLI();
        Field field = GeolocationCLI.class.getDeclaredField("geolocationService");
        field.setAccessible(true);
        field.set(cli, mockGeolocationService);

        commandLine = new CommandLine(cli);
    }

    @AfterEach
    void tearDown() {
        // Restore original stdout and stderr
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Should return error when no locations are provided")
    void testNoLocationsProvided() {
        int exitCode = commandLine.execute();
        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Error: Please provide at least one location."));
    }

    @Test
    @DisplayName("Should successfully fetch location data")
    void testSuccessfulFetch() {
        when(mockGeolocationService.fetchLocationData("Madison, WI"))
                .thenReturn("Input: Madison, WI → Location: Madison, Lat: 43.074761, Lon: -89.3837613");

        int exitCode = commandLine.execute("Madison, WI");

        assertEquals(0, exitCode);
        assertTrue(outContent.toString().contains("Input: Madison, WI → Location: Madison, Lat: 43.074761, Lon: -89.3837613"));
    }

    @Test
    @DisplayName("Should return error when GeolocationException occurs")
    void testGeolocationExceptionHandling() {
        when(mockGeolocationService.fetchLocationData("InvalidCity, ZZ"))
                .thenThrow(new GeolocationException("No results found for InvalidCity, ZZ"));

        int exitCode = commandLine.execute("InvalidCity, ZZ");

        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Error: No results found for InvalidCity, ZZ"));
    }

    @Test
    @DisplayName("Should handle multiple locations with mixed results")
    void testMultipleLocations_MixedResults() {
        when(mockGeolocationService.fetchLocationData("Madison, WI"))
                .thenReturn("Input: Madison, WI → Location: Madison, Lat: 43.074761, Lon: -89.3837613");

        when(mockGeolocationService.fetchLocationData("InvalidCity, ZZ"))
                .thenThrow(new GeolocationException("No results found for InvalidCity, ZZ"));

        int exitCode = commandLine.execute("Madison, WI", "InvalidCity, ZZ");

        assertEquals(1, exitCode);
        assertTrue(outContent.toString().contains("Input: Madison, WI → Location: Madison, Lat: 43.074761, Lon: -89.3837613"));
        assertTrue(errContent.toString().contains("Error: No results found for InvalidCity, ZZ"));
    }

    @Test
    @DisplayName("Should return error when too many locations are provided")
    void testTooManyLocationsProvided() {
        String[] locations = {
                "New York, NY", "Los Angeles, CA", "Chicago, IL", "Houston, TX", "Phoenix, AZ",
                "Philadelphia, PA", "San Antonio, TX", "San Diego, CA", "Dallas, TX", "San Jose, CA",
                "ExtraLocation"  // 11th location, exceeding limit
        };

        int exitCode = commandLine.execute(locations);

        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Error: Too many locations provided."));
    }
}
