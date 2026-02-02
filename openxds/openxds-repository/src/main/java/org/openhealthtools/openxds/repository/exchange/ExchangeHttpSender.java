package org.openhealthtools.openxds.repository.exchange;

import org.apache.axiom.om.util.Base64;
import org.openhealthtools.openxds.repository.entity.XdsUploadDocumentRequest;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements methods that can send requests to Exchange Web Service to Upload, Download, or Delete a document.
 *
 * @author averazub
 * Created on 9/30/2016.
 */
@Component
public class ExchangeHttpSender {

    private String exchangeHomeUrl;
    private String authUsername;
    private String authPassword;

    public String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
    public String DOCUMENT_UPLOAD_URL = "/documents/xds/upload";
    public String DOCUMENT_DOWNLOAD_URL = "/documents/xds/download";
    public String DOCUMENT_DELETE_URL = "/documents/xds/delete";

    public String AUTHENTICATE_URL = "/j_spring_security_check";


    public String getExchangeHomeUrl() {
        return exchangeHomeUrl;
    }

    public void setExchangeHomeUrl(String exchangeHomeUrl) {
        this.exchangeHomeUrl = exchangeHomeUrl;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public XdsUploadDocumentRequest sendGetDocumentRequest(String documentUniqueId) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = createHttpEntity(null, null);
        String url = exchangeHomeUrl + DOCUMENT_DOWNLOAD_URL + "?documentUniqueId=" + documentUniqueId;

        ResponseEntity<XdsUploadDocumentRequest> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, XdsUploadDocumentRequest.class, new Object[]{});
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new IOException("I/O exception with Exchange!");
        }
        return responseEntity.getBody();
    }

    public void sendPostDocumentRequest(XdsUploadDocumentRequest request) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity httpEntity = createHttpEntity(request, JSON_CONTENT_TYPE);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(exchangeHomeUrl + DOCUMENT_UPLOAD_URL, HttpMethod.POST, httpEntity, String.class, new Object[]{});
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throw new IOException("I/O exception with Exchange!");
            }
        } catch (HttpServerErrorException e) {
            throw new IOException(e.getResponseBodyAsString());
        }
    }

    public void sendDeleteDocumentRequest(String documentUniqueId) throws IOException {
        final List<HttpMessageConverter<?>> converterList = new ArrayList<HttpMessageConverter<?>>();
        converterList.add(new FormHttpMessageConverter());

        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(converterList);

        HttpEntity entity = createHttpEntity(null, null);
        String url = exchangeHomeUrl + DOCUMENT_DELETE_URL + "?documentUniqueId=" + documentUniqueId;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class, new Object[]{});
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new IOException("I/O exception with Exchange!");
        }
    }


//    private String authenticate() {
//        try {
//            final List<HttpMessageConverter<?>> converterList = new ArrayList<HttpMessageConverter<?>>();
//            converterList.add (new FormHttpMessageConverter());
//
//            final RestTemplate restTemplate = new RestTemplate ();
//
//            restTemplate.setMessageConverters (converterList);
//
//            MultiValueMap<String, String> authMap = new LinkedMultiValueMap<String, String>();
//            authMap.add ("username", exchangeXdsUser);
//            authMap.add ("password", exchangeXdsPassword);
//            authMap.add ("company", exchangeXdsCompany);
//
//            HttpEntity userEntity = createHttpEntity(authMap, false, null);
//
//            final ResponseEntity<String> responseEntity = restTemplate.postForEntity (exchangeHomeUrl + AUTHENTICATE_URL, userEntity, String.class);
//
//            if (responseEntity.getStatusCode () == HttpStatus.FOUND) {
//                return responseEntity.getHeaders ().get ("Set-Cookie").get (0).split (";") [0];
//            } else {
//                System.err.println("Header \"session-token\" not found!");
//                throw new RuntimeException ();
//            }
//        } catch (final Throwable th) {
//            System.err.println(th.getMessage ());
//            throw new RuntimeException (th);
//        }
//    }


    private <REQUEST> HttpEntity<?> createHttpEntity(final REQUEST request, String contentType) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        if (contentType != null) httpHeaders.add("Content-Type", contentType);
        String basicAuth = Base64.encode((authUsername + ":" + authPassword).getBytes());
        httpHeaders.add("Authorization", "Basic " + basicAuth);

        HttpEntity<?> httpEntity;

        if (request == null) {
            httpEntity = new HttpEntity<REQUEST>(httpHeaders);
        } else {
            httpEntity = new HttpEntity<REQUEST>(request, httpHeaders);
        }

        return httpEntity;
    }


}
