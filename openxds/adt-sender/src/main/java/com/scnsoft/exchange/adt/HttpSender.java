package com.scnsoft.exchange.adt;

import com.scnsoft.exchange.adt.entity.ResponseDto;
import com.scnsoft.exchange.adt.ssl.SslConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by averazub on 9/30/2016.
 */
@Component
public class HttpSender {

    public String AUTHENTICATE_URL = "/j_spring_security_check";


    @Value("${xds.exchange.keystore}")
    private String keyStorePath;

    @Value("${xds.exchange.keystore.password}")
    private String keyStorePassword;

    @Value("${xds.exchange.truststore}")
    private String trustStorePath;

    @Value("${xds.exchange.truststore.password}")
    private String trustStorePassword;



    public ResponseDto sendRequest(String url, Map<String, String> headers, String requestBody) {
        try {

            SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslConnectionFactory.getSSLSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(sslConnectionFactory.getLocalhostResolvedHostnameVerifier());

            RestTemplate restTemplate = new RestTemplate(/*new HttpComponentsClientHttpRequestFactory()*/);
            HttpEntity<String> entity = createHttpEntity(requestBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            ResponseDto responseDto = new ResponseDto();
            responseDto.setStatusCode(responseEntity.getStatusCode().value());
            responseDto.setStatus(responseEntity.getStatusCode().name());
            responseDto.setResponseBody(responseEntity.getBody());
            return responseDto;
        } catch (ResourceAccessException e) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setStatusCode(-1);
            responseDto.setStatus("ERROR_CONNECTION_REFUSED");
            responseDto.setResponseBody(e.getMessage());
            return responseDto;
        }

    }



    private static <REQUEST> HttpEntity<REQUEST> createHttpEntity (final REQUEST request, Map<String, String> headers) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, String> header: headers.entrySet()) {
            httpHeaders.add(header.getKey(), header.getValue());
        }

        HttpEntity<REQUEST> httpEntity;

        if (request == null) {
            httpEntity = new HttpEntity<REQUEST> (httpHeaders);
        } else {
            httpEntity = new HttpEntity<REQUEST> (request, httpHeaders);
        }

        return httpEntity;
    }

/*
    public XdsUploadDocumentRequest sendGetDocumentRequest(String documentUniqueId) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = createHttpEntity(null, true, null);
        String url = exchangeHomeUrl + DOCUMENT_DOWNLOAD_URL + "?documentUniqueId="+documentUniqueId;

        ResponseEntity<XdsUploadDocumentRequest> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, XdsUploadDocumentRequest.class, new Object[]{});
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new IOException("I/O exception with Exchange!");
        }
        return responseEntity.getBody();
    }

    public void sendPostDocumentRequest(XdsUploadDocumentRequest request) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity httpEntity = createHttpEntity(request, true, JSON_CONTENT_TYPE);
        ResponseEntity<String> responseEntity = restTemplate.exchange(exchangeHomeUrl + DOCUMENT_UPLOAD_URL, HttpMethod.POST, httpEntity, String.class, new Object[]{});
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new IOException("I/O exception with Exchange!");
        }
    }

    public void sendDeleteDocumentRequest(String documentUniqueId) throws IOException {
        final List<HttpMessageConverter<?>> converterList = new ArrayList<HttpMessageConverter<?>>();
        converterList.add (new FormHttpMessageConverter());

        final RestTemplate restTemplate = new RestTemplate ();
        restTemplate.setMessageConverters (converterList);

        HttpEntity entity = createHttpEntity(null, true, null);
        String url = exchangeHomeUrl + DOCUMENT_DELETE_URL + "?documentUniqueId="+documentUniqueId;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class, new Object[]{});
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new IOException("I/O exception with Exchange!");
        }
    }


    private String authenticate() {
        try {
            final List<HttpMessageConverter<?>> converterList = new ArrayList<HttpMessageConverter<?>>();
            converterList.add (new FormHttpMessageConverter());

            final RestTemplate restTemplate = new RestTemplate ();

            restTemplate.setMessageConverters (converterList);

            MultiValueMap<String, String> authMap = new LinkedMultiValueMap<String, String>();
            authMap.add ("username", exchangeXdsUser);
            authMap.add ("password", exchangeXdsPassword);
            authMap.add ("company", exchangeXdsCompany);

            HttpEntity userEntity = createHttpEntity(authMap, false, null);

            final ResponseEntity<String> responseEntity = restTemplate.postForEntity (exchangeHomeUrl + AUTHENTICATE_URL, userEntity, String.class);

            if (responseEntity.getStatusCode () == HttpStatus.FOUND) {
                return responseEntity.getHeaders ().get ("Set-Cookie").get (0).split (";") [0];
            } else {
                System.err.println("Header \"session-token\" not found!");
                throw new RuntimeException ();
            }
        } catch (final Throwable th) {
            System.err.println(th.getMessage ());
            throw new RuntimeException (th);
        }
    }


    private <REQUEST> HttpEntity<?> createHttpEntity (final REQUEST request, boolean tokenRequired, String contentType) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        if (contentType!=null) httpHeaders.add ("Content-Type", contentType);
        if (tokenRequired) {
            String token = authenticate();
            httpHeaders.add ("Cookie", token);
        }

        HttpEntity<?> httpEntity;

        if (request == null) {
            httpEntity = new HttpEntity<REQUEST> (httpHeaders);
        } else {
            httpEntity = new HttpEntity<REQUEST> (request, httpHeaders);
        }

        return httpEntity;
    }


*/

}
