package com.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class GeolocationService {
    private static final String API_KEY = "f897a99d971b5eef57be6fafa0d83239";
    private static final String BASE_URL = "http://api.openweathermap.org/geo/1.0/";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GeolocationService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches geolocation data for a given city/state or ZIP code.
     *
     * @param location City/state (e.g., "Madison, WI") or ZIP code (e.g., "10001")
     * @return Formatted location data including latitude, longitude, and name
     */
    public String fetchLocationData(String location) {
        String url;

        if (location.matches("\\d{5}")) {
            // ZIP Code lookup (returns a single JSON object)
            url = String.format("%szip?zip=%s,US&appid=%s", BASE_URL, location, API_KEY);
        } else {
            // City/State lookup (returns an array of results)
            url = String.format("%sdirect?q=%s,US&limit=1&appid=%s", BASE_URL, location, API_KEY);
        }

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Failed to fetch data for " + location + ": " + response.message();
            }

            String responseBody = response.body().string();
            return parseResponse(responseBody, location);
        } catch (IOException e) {
            return "Error fetching data for " + location + ": " + e.getMessage();
        }
    }

    /**
     * Parses API response and extracts relevant geolocation data.
     *
     * @param response JSON response string from API
     * @param location User input location (used for error messages)
     * @return Formatted string with location name, latitude, and longitude
     */
    private String parseResponse(String response, String location) {
        try {
            if (response == null || response.isBlank()) {
                return "Input: " + location + " → No response from API.";
            }

            JsonNode root = objectMapper.readTree(response);

            if (root.isArray() && root.size() > 0) {
                // City/State API returns an array, use the first result
                return extractLocationInfo(location, root.get(0));
            } else if (root.isObject()) {
                // ZIP Code API returns a single JSON object
                return extractLocationInfo(location, root);
            }

        } catch (Exception e) {
            return "Input: " + location + " → Error parsing response: " + e.getMessage();
        }
        return "Input: " + location + " → No results found.";
    }

    /**
     * Extracts location name, latitude, and longitude from API response.
     *
     * @param node JSON node containing location data
     * @return Formatted string with extracted data
     */
    private String extractLocationInfo(String location, JsonNode node) {
        return String.format("Input: %s → Location: %s, Lat: %s, Lon: %s",
                location,
                node.has("name") ? node.get("name").asText() : "Unknown",
                node.has("lat") ? node.get("lat").asText() : "Unknown",
                node.has("lon") ? node.get("lon").asText() : "Unknown");
    }
}
