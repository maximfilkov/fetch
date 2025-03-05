package com.fetch.service;

import com.fetch.config.ConfigReader;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GeolocationServiceTest {

    @Mock
    private OkHttpClient mockClient;
    @Mock
    private Call mockCall;
    @Mock
    private ResponseBody mockResponseBody;

    @InjectMocks
    private GeolocationService geolocationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should handle successful response correctly")
    void testFetchLocationData_Success() throws IOException {
        mockStaticConfigReader("http://mock.api", "{base_url}/zip?zip={zip}&appid={api_key}", "{base_url}/direct?q={location}&appid={api_key}");

        String jsonResponse = "[{\"name\":\"Madison\",\"lat\":43.074761,\"lon\":-89.3837613}]";

        // Mock response object manually because it's final
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("http://mock.api").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(jsonResponse, MediaType.get("application/json")))
                .build();

        when(mockClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        String result = geolocationService.fetchLocationData("Madison, WI");

        assertEquals("Input: Madison, WI → Location: Madison, Lat: 43.074761, Lon: -89.3837613", result);
    }

    private void mockStaticConfigReader(String baseUrl, String zipEndpoint, String directEndpoint) {
        try (MockedStatic<ConfigReader> mockedConfigReader = mockStatic(ConfigReader.class)) {
            mockedConfigReader.when(() -> ConfigReader.getProperty("base_url")).thenReturn(baseUrl);
            mockedConfigReader.when(() -> ConfigReader.getFormattedProperty(eq("zip_endpoint"), any())).thenReturn(zipEndpoint);
            mockedConfigReader.when(() -> ConfigReader.getFormattedProperty(eq("direct_endpoint"), any())).thenReturn(directEndpoint);
        }
    }
}
