package com.fetch.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeolocationExceptionTest {

    @Test
    @DisplayName("Should create GeolocationException with only message")
    void testGeolocationException_MessageOnly() {
        GeolocationException exception = new GeolocationException("Test error message");

        assertEquals("Test error message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should create GeolocationException with message and cause")
    void testGeolocationException_MessageAndCause() {
        Throwable cause = new IllegalArgumentException("Invalid input");
        GeolocationException exception = new GeolocationException("Test error message", cause);

        assertEquals("Test error message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
