package com.fetch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.config.ConfigReader;
import com.fetch.exceptions.GeolocationException;
import okhttp3.*;
import java.io.IOException;
import java.util.Objects;

/**
 * Service for fetching geolocation data using the OpenWeather Geocoding API.
 * <p>
 * This service supports fetching location details (latitude, longitude, and name)
 * for a given city/state or ZIP code by making HTTP requests to the API.
 */
public class GeolocationService {
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    /**
     * Initializes a new instance of {@code GeolocationService} with an HTTP client
     * and JSON parser.
     */
    public GeolocationService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public GeolocationService(OkHttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches geolocation data for a given location input.
     * <p>
     * The input can be a city/state combination (e.g., "Madison, WI") or a ZIP code (e.g., "10001").
     * The method determines the appropriate API endpoint and retrieves the corresponding geolocation details.
     *
     * @param location A city/state name or ZIP code.
     * @return A formatted string containing the location name, latitude, and longitude.
     * @throws GeolocationException if the request fails or the response cannot be processed.
     */
    public String fetchLocationData(String location) {
        String baseUrl = ConfigReader.getProperty("base_url");
        String apiKey = ConfigReader.getProperty("api_key");

        if (baseUrl == null || apiKey == null) {
            throw new GeolocationException("BASE_URL or API_KEY is not configured properly.");
        }

        String url = location.matches("\\d{5}")
                ? ConfigReader.getFormattedProperty("zip_endpoint",
                "{base_url}", baseUrl,
                "{zip}", location,
                "{api_key}", apiKey)
                : ConfigReader.getFormattedProperty("direct_endpoint",
                "{base_url}", baseUrl,
                "{location}", location,
                "{api_key}", apiKey);

        if (url == null || url.isBlank()) {
            throw new GeolocationException("Invalid API endpoint configuration.");
        }

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new GeolocationException("Failed to fetch data for " + location + ": " + response.message());
            }

            if (response.body() == null) {
                throw new GeolocationException("Empty response body for " + location);
            }

            String responseBody = Objects.requireNonNull(response.body()).string();
            return parseResponse(responseBody, location);
        } catch (IOException e) {
            throw new GeolocationException("Error fetching data for " + location, e);
        }
    }

    /**
     * Parses the JSON response from the API and extracts relevant location details.
     *
     * @param response The JSON response body.
     * @param location The user-input location.
     * @return A formatted string with the location name, latitude, and longitude.
     * @throws GeolocationException if parsing fails or no results are found.
     */
    private String parseResponse(String response, String location) {
        try {
            if (response.isBlank()) {
                throw new GeolocationException("No response from API for " + location);
            }

            JsonNode root = objectMapper.readTree(response);

            if (root.isArray() && root.size() > 0) {
                return extractLocationInfo(location, root.get(0));
            } else if (root.isObject()) {
                return extractLocationInfo(location, root);
            }
        } catch (Exception e) {
            throw new GeolocationException("Error parsing response for " + location, e);
        }

        throw new GeolocationException("No results found for " + location);
    }

    /**
     * Extracts the location name, latitude, and longitude from a JSON node.
     *
     * @param location The original user input.
     * @param node     The JSON node containing location details.
     * @return A formatted string with extracted geolocation data.
     */
    private String extractLocationInfo(String location, JsonNode node) {
        return String.format("Input: %s → Location: %s, Lat: %s, Lon: %s",
                location,
                node.has("name") ? node.get("name").asText() : "Unknown",
                node.has("lat") ? node.get("lat").asText() : "Unknown",
                node.has("lon") ? node.get("lon").asText() : "Unknown");
    }
}
