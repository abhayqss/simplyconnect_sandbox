package com.scnsoft.eldermark.service.pointclickcare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.support.RestGatewaySupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PointClickCareAuthenticationTokenManagerImplTest {
    private final String clientId = "cliendId";
    private final String clientSecret = "clientSecret";
    private final String authEndpoint = "http://localhost:8080/authPCC";

    private MockRestServiceServer mockServer;

    private PointClickCareAuthenticationTokenManagerImpl instance;

    @BeforeEach
    public void setUp() {
        RestGatewaySupport gateway = new RestGatewaySupport();
        instance = new PointClickCareAuthenticationTokenManagerImpl(clientId, clientSecret, authEndpoint, jsonRestTemplateBuilder());

        gateway.setRestTemplate(instance.getRestTemplate());
        mockServer = MockRestServiceServer.createServer(gateway);
    }

    private RestTemplateBuilder jsonRestTemplateBuilder() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());

        var builder = new RestTemplateBuilder().messageConverters(converter);
        return builder;
    }

    private String buildJsonResponse(String token, int expiresIn) {
        return "{\"expires_in\":" + expiresIn + ",\"access_token\":\"" + token + "\"}";
    }

    @Test
    void getBearerToken_WhenNotExpired_tokenIsReused() throws URISyntaxException {
        var token = "eyJhbGciOmdCIsImV4";
        var expiresIn = 1000;

        setUpMockServerExpectations(ExpectedCount.once())
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(buildJsonResponse(token, expiresIn))
                );

        var actualToken1 = instance.getBearerToken();
        var actualToken2 = instance.getBearerToken();

        assertEquals(token, actualToken1);
        assertEquals(token, actualToken2);
    }

    @Test
    void getBearerToken_WhenExpired_newTokenFetched() throws URISyntaxException {
        var token1 = "eyJhbGciOmdCIsImV4";
        var token2 = "eyJhbGciOmdCIsImV56";
        var expiresIn = 0;

        setUpMockServerExpectations(ExpectedCount.once())
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(buildJsonResponse(token1, expiresIn))
                );
        setUpMockServerExpectations(ExpectedCount.once())
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(buildJsonResponse(token2, expiresIn))
                );

        var actualToken1 = instance.getBearerToken();
        var actualToken2 = instance.getBearerToken();

        assertEquals(token1, actualToken1);
        assertEquals(token2, actualToken2);
    }

    @Test
    void getBearerToken_WhenError_Throws() throws URISyntaxException {
        setUpMockServerExpectations(ExpectedCount.once())
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"err\":\"some error message\"}"));

        assertThrows(PointClickCareApiException.class, () -> instance.getBearerToken());
    }

    private ResponseActions setUpMockServerExpectations(ExpectedCount expectedCount) throws URISyntaxException {
        var map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "client_credentials");
        return mockServer.expect(expectedCount, requestTo(new URI(authEndpoint)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + Base64Utils.encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8))))
                .andExpect(header("Content-type", "application/x-www-form-urlencoded;charset=UTF-8"))
                .andExpect(content().formData(map));
    }
}