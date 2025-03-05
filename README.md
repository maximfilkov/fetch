# Geolocation CLI Utility

This command-line utility fetches geolocation data based on a given city/state or ZIP code. It retrieves latitude, longitude, and place name information from the OpenWeather Geocoding API.

## Prerequisites

- **Java**: This application requires Java (version 11 or later). You can install Java using the following methods:

    - **macOS (Homebrew)**:
      ```sh
      brew install openjdk@11
      ```

    - **Windows (Chocolatey)**:
      ```sh
      choco install openjdk11
      ```

    - **Linux (Debian-based)**:
      ```sh
      sudo apt install openjdk-11-jdk
      ```

  After installation, verify Java is available by running:
  ```sh
  java -version
  ```

- **Maven**: Ensure Maven is installed before building the application.

    - **macOS (Homebrew)**:
      ```sh
      brew install maven
      ```

    - **Windows (Chocolatey)**:
      ```sh
      choco install maven
      ```

    - **Linux (Debian-based)**:
      ```sh
      sudo apt install maven
      ```

  After installation, ensure `JAVA_HOME` is correctly set:
  ```sh
  export JAVA_HOME=$(/usr/libexec/java_home)
  ```

  Verify Maven installation with:
  ```sh
  mvn -version
  ```

## Environment Variables

Before running the application, set the required API key:

```sh
export API_KEY="your_openweather_api_key"
```

For Windows (Command Prompt):
```cmd
set API_KEY=your_openweather_api_key
```

For Windows (PowerShell):
```powershell
$env:API_KEY="your_openweather_api_key"
```

## Building the Application

To build the application, run:

```sh
mvn clean package
```

This generates a JAR file inside the `target/` directory.

## Running the Application

Once built, run the application with:

```sh
java -jar target/Fetch-1.0-SNAPSHOT.jar "Madison, WI" "12345"
```

### Example Usage

```sh
java -jar target/Fetch-1.0-SNAPSHOT.jar "Madison, WI" "10001" "Chicago, IL"
```

### Expected Output:

```
Input: Madison, WI → Location: Madison, Lat: 43.074761, Lon: -89.3837613
Input: 10001 → Location: New York, Lat: 40.7484, Lon: -73.9967
Input: Chicago, IL → Location: Chicago, Lat: 41.8755616, Lon: -87.6244212
```

## Running Tests

To run the test suite:

```sh
mvn test
```

Ensure the `API_KEY` environment variable is set before running tests:

```sh
export API_KEY="mock-api-key"
```

For Windows (Command Prompt):

```cmd
set API_KEY=mock-api-key
```

For Windows (PowerShell):

```powershell
$env:API_KEY="mock-api-key"
```

