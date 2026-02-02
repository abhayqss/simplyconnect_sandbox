package com.scnsoft.eldermark.consana.sync.server.consana.template;

import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2TokenProvider;
import org.apache.http.client.HttpClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsanaRestTemplateServiceImpl implements ConsanaRestTemplateService {

    private final ConsanaOauth2TokenProvider consanaOauth2TokenProvider;
    private final RestTemplate restTemplate;

    public ConsanaRestTemplateServiceImpl(ConsanaOauth2TokenProvider consanaOauth2TokenProvider, HttpClient consanaHttpClient) {
        this.consanaOauth2TokenProvider = consanaOauth2TokenProvider;
        this.restTemplate = initRestTemplate(consanaHttpClient);
    }

    private RestTemplate initRestTemplate(HttpClient consanaHttpClient) {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(consanaHttpClient));
    }

    @Override
    public RestTemplate getConsanaRestTemplate() {
        return restTemplate;
    }

    @Override
    public HttpHeaders createConsanaHttpHeaders(String xclOrganizationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + consanaOauth2TokenProvider.getActiveToken());
        headers.set("x-xcl-organization", xclOrganizationId);
        return headers;
    }

}
