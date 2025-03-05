package com.fetch;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "geoloc-util", mixinStandardHelpOptions = true, description = "Fetch geolocation data")
public class GeolocationCLI implements Callable<Integer> {

    @CommandLine.Parameters(index = "0..*", description = "Location names or zip codes")
    private String[] locations;

    private final GeolocationService geolocationService = new GeolocationService();

    @Override
    public Integer call() {
        if (locations == null || locations.length == 0) {
            System.out.println("Please provide at least one location.");
            return 1;
        }

        for (String location : locations) {
            System.out.println(geolocationService.fetchLocationData(location));
        }

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GeolocationCLI()).execute(args);
        System.exit(exitCode);
    }
}
