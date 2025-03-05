package com.fetch.exceptions;

/**
 * Exception thrown when an error occurs while fetching geolocation data.
 */
public class GeolocationException extends RuntimeException {
    public GeolocationException(String message) {
        super(message);
    }

    public GeolocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
