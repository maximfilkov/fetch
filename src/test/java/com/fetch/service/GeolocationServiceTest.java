package com.fetch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.exceptions.GeolocationException;
import okhttp3.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GeolocationServiceTest {

    @Mock
    private OkHttpClient mockClient;
    @Mock
    private Call mockCall;

    @InjectMocks
    private GeolocationService geolocationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("base_url", "http://mock.api");

        // Manually inject the mock client since it's not set automatically
        geolocationService = new GeolocationService(mockClient, new ObjectMapper());

        // Ensure mockClient.newCall() always returns the mocked Call
        when(mockClient.newCall(any())).thenReturn(mockCall);
    }

    @Test
    @DisplayName("Should handle successful response correctly")
    void testFetchLocationData_Success() throws IOException {
        String jsonResponse = "[{\"name\":\"Madison\",\"lat\":43.074761,\"lon\":-89.3837613}]";

        Response mockResponse = createMockResponse(200, jsonResponse);
        when(mockClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        String result = geolocationService.fetchLocationData("Madison, WI");
        assertEquals("Input: Madison, WI → Location: Madison, Lat: 43.074761, Lon: -89.3837613", result);
    }

    @Test
    @DisplayName("Should throw exception on API failure")
    void testFetchLocationData_ApiFailure() throws IOException {
        Response mockResponse = createMockResponse(500, "Internal Server Error");

        when(mockClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        GeolocationException thrown = assertThrows(GeolocationException.class, () ->
                geolocationService.fetchLocationData("New York, NY")
        );

        assertEquals("Failed to fetch data for New York, NY: Error", thrown.getMessage());
    }

    private Response createMockResponse(int statusCode, String body) {
        return new Response.Builder()
                .request(new Request.Builder().url("http://mock.api").build())
                .protocol(Protocol.HTTP_1_1)
                .code(statusCode)
                .message(statusCode == 200 ? "OK" : "Error")
                .body(body != null ? ResponseBody.create(body, MediaType.get("application/json")) : null)
                .build();
    }
}
