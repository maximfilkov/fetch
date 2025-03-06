package com.fetch;

import com.fetch.config.ConfigReader;
import com.fetch.exceptions.GeolocationException;
import com.fetch.service.GeolocationService;
import picocli.CommandLine;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * A command-line utility for fetching geolocation data based on city/state or ZIP code.
 * <p>
 * This class uses the {@link GeolocationService} to fetch latitude, longitude, and location details.
 * It is implemented using the {@code picocli} library for command-line argument parsing.
 */
@CommandLine.Command(
        name = "geoloc-util",
        mixinStandardHelpOptions = true,
        description = "Fetch geolocation data"
)
public class GeolocationCLI implements Callable<Integer> {

    @CommandLine.Parameters(index = "0..*", description = "Location names or zip codes")
    private List<String> locations; // Automatically injected with Picocli

    private final GeolocationService geolocationService = new GeolocationService();
    private final Map<String, String> cache = new HashMap<>(); // Caching previous responses
    private final int maxLocations;

    /**
     * Constructor initializes max locations limit from config.
     */
    public GeolocationCLI() {
        String maxLocationsConfig = ConfigReader.getProperty("max_locations");
        this.maxLocations = (maxLocationsConfig != null) ? Integer.parseInt(maxLocationsConfig) : 10;
    }

    /**
     * Processes the command-line inputs and fetches geolocation data for each provided location.
     *
     * @return an exit code (0 for success, 1 for errors).
     */
    @Override
    public Integer call() {
        if (locations == null || locations.isEmpty()) {
            System.err.println("Error: Please provide at least one location.");
            return 1;
        }

        // Enforce the maximum allowed locations
        if (locations.size() > maxLocations) {
            System.err.printf("Error: Too many locations provided. Maximum allowed is %d.%n", maxLocations);
            return 1;
        }

        boolean hasError = false; // Track if any errors occurred

        for (String location : locations) {
            if (cache.containsKey(location)) {
                // Use Cached Result Instead of API Call
                System.out.println(cache.get(location));
                continue;
            }

            try {
                String result = geolocationService.fetchLocationData(location);
                cache.put(location, result); // Store in Cache
                System.out.println(result);
            } catch (GeolocationException e) {
                System.err.println("Error: " + e.getMessage());
                hasError = true;
            }
        }

        return hasError ? 1 : 0;  // Return 1 if any errors occurred, else return 0
    }

    /**
     * Main entry point for the CLI application.
     *
     * @param args Command-line arguments specifying locations.
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new GeolocationCLI()).execute(args);
        System.exit(exitCode);
    }
}
