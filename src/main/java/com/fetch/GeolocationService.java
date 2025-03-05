package com.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class GeolocationService {
    private static final String API_KEY = "f897a99d971b5eef57be6fafa0d83239";
    private static final String BASE_URL = ConfigReader.getProperty("base_url");
    private static final String ZIP_ENDPOINT_TEMPLATE = ConfigReader.getProperty("zip_endpoint");
    private static final String DIRECT_ENDPOINT_TEMPLATE = ConfigReader.getProperty("direct_endpoint");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GeolocationService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String fetchLocationData(String location) {
        String url;

        if (location.matches("\\d{5}")) {
            url = ConfigReader.getFormattedProperty("zip_endpoint",
                    "{base_url}", BASE_URL,
                    "{zip}", location,
                    "{api_key}", API_KEY);
        } else {
            url = ConfigReader.getFormattedProperty("direct_endpoint",
                    "{base_url}", BASE_URL,
                    "{location}", location,
                    "{api_key}", API_KEY);
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

    private String parseResponse(String response, String location) {
        try {
            if (response == null || response.isBlank()) {
                return "Input: " + location + " → No response from API.";
            }

            JsonNode root = objectMapper.readTree(response);

            if (root.isArray() && root.size() > 0) {
                return extractLocationInfo(location, root.get(0));
            } else if (root.isObject()) {
                return extractLocationInfo(location, root);
            }

        } catch (Exception e) {
            return "Input: " + location + " → Error parsing response: " + e.getMessage();
        }
        return "Input: " + location + " → No results found.";
    }

    private String extractLocationInfo(String location, JsonNode node) {
        return String.format("Input: %s → Location: %s, Lat: %s, Lon: %s",
                location,
                node.has("name") ? node.get("name").asText() : "Unknown",
                node.has("lat") ? node.get("lat").asText() : "Unknown",
                node.has("lon") ? node.get("lon").asText() : "Unknown");
    }
}
