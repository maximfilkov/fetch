# Geolocation CLI - One-Pager Design Document

## Architecture & Design

### High-Level Flow
1. The user provides one or more location inputs (city/state or ZIP code).
2. The CLI processes the input, ensuring it does not exceed the **configurable maximum limit**.
3. A **cache** is used to prevent duplicate API calls for the same location.
4. If the location is not cached, the application makes a request to the **OpenWeather Geocoding API**.
5. The response is parsed, and relevant data (latitude, longitude, location name) is extracted.
6. The result is displayed to the user.
7. If an error occurs (e.g., invalid location, API failure), an appropriate message is printed.

### Key Components
| Component               | Description |
|------------------------|-------------|
| `GeolocationCLI`       | Handles CLI arguments, input validation, caching, and prints results/errors. |
| `GeolocationService`   | Communicates with OpenWeather API, fetches & parses geolocation data. |
| `ConfigReader`         | Loads configuration values (e.g., API base URL, max locations) from `config.properties`. |
| `GeolocationException` | Custom exception class for error handling. |

###  Error Handling

| Scenario                         | Error Message |
|----------------------------------|--------------|
| No locations provided | `Error: Please provide at least one location.` |
| Too many locations | `Error: Too many locations provided. Maximum allowed is 10.` |
| Invalid location | `Error: No results found for UnknownPlace` |
| API failure (e.g., 500) | `Error: Failed to fetch data for [Location]: Internal Server Error` |
