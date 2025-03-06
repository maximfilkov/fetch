package integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GeolocationCLIIntegrationTest {

    private static final String JAR_PATH = "target/Fetch-1.0-SNAPSHOT.jar";
    private static final String API_KEY = System.getenv("API_KEY"); // Ensure this is set in CI/CD or manually

    @BeforeAll
    static void buildProject() throws Exception {
        // Ensure the JAR is built before running tests
        if (!Files.exists(Paths.get(JAR_PATH))) {
            System.out.println("JAR not found. Building project...");
            Process buildProcess = new ProcessBuilder("mvn", "clean", "package", "-DskipTests=true")
                    .redirectErrorStream(true)
                    .start();
            buildProcess.waitFor();
            assertEquals(0, buildProcess.exitValue(), "Maven build failed.");
        }
    }

    @Test
    @DisplayName("Integration Test: Run CLI with valid inputs")
    void testCLIWithValidInputs() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-jar", JAR_PATH, "Madison, WI", "10001"
        );
        processBuilder.environment().put("API_KEY", API_KEY);

        Process process = processBuilder.start();

        // Capture output
        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();

        assertEquals(0, exitCode, "CLI should exit with code 0 on success");
        assertTrue(output.contains("Madison") && output.contains("Lat") && output.contains("Lon"),
                "Expected geolocation data in the output");
    }

    @Test
    @DisplayName("Integration Test: Run CLI with invalid input")
    void testCLIWithInvalidInput() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-jar", JAR_PATH, "UnknownPlace"
        );
        processBuilder.environment().put("API_KEY", API_KEY);
        processBuilder.redirectErrorStream(true); // Capture both stdout and stderr

        Process process = processBuilder.start();

        // Capture both stdout and stderr
        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();

        assertEquals(1, exitCode, "CLI should exit with code 1 on failure");
        assertTrue(output.contains("No results found") || output.contains("Error"),
                "Expected failure message in the output, but got:\n" + output);
    }
}
