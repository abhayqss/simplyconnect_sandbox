package com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.impl;

import com.scnsoft.eldermark.consana.sync.common.config.ConsanaOauth2Context;
import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2Response;
import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2RestTemplate;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsanaOauth2RestTemplateImpl implements ConsanaOauth2RestTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaOauth2RestTemplateImpl.class);

    private final String url;
    private final HttpEntity<MultiValueMap<String, String>> requestEntity;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    @Autowired
    public ConsanaOauth2RestTemplateImpl(ConsanaOauth2Context context,
                                         HttpClient consanaHttpClient,
                                         RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
        validateContext(context);
        this.url = context.getUrl();
        this.requestEntity = buildRequest(context);
        this.restTemplate = initRestTemplate(consanaHttpClient);
    }

    private void validateContext(ConsanaOauth2Context context) throws InvalidPropertyException {
        if (StringUtils.isEmpty(context.getUrl())) {
            throw new InvalidPropertyException(ConsanaOauth2Context.class, "consana.auth.oauth2.url", "Please specify url");
        }
        if (StringUtils.isEmpty(context.getGrantType())) {
            throw new InvalidPropertyException(ConsanaOauth2Context.class, "consana.auth.oauth2.grantType", "Please specify grant type");
        }
        if (StringUtils.isEmpty(context.getClientId())) {
            throw new InvalidPropertyException(ConsanaOauth2Context.class, "consana.auth.oauth2.clientId", "Please specify client id");
        }
        if (StringUtils.isEmpty(context.getClientSecret())) {
            throw new InvalidPropertyException(ConsanaOauth2Context.class, "consana.auth.oauth2.clientSecret", "Please specify client secret");
        }
        if (StringUtils.isEmpty(context.getScopes())) {
            throw new InvalidPropertyException(ConsanaOauth2Context.class, "consana.auth.oauth2.scopes", "Please specify scopes");
        }
    }

    private RestTemplate initRestTemplate(HttpClient httpClient) {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Override
    public ConsanaOauth2Response sendPostForToken() {
        var response = retryTemplate.execute((RetryCallback<ConsanaOauth2Response, RestClientResponseException>) retryContext ->
                restTemplate.postForObject(url, requestEntity, ConsanaOauth2Response.class));
        logger.debug("received oauth2 token: {}", response);
        return response;
    }

    private HttpEntity<MultiValueMap<String, String>> buildRequest(ConsanaOauth2Context context) {
        return new HttpEntity<>(buildBody(context), buildHeaders());
    }

    private MultiValueMap<String, String> buildBody(ConsanaOauth2Context context) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", context.getGrantType());
        map.add("client_id", context.getClientId());
        map.add("client_secret", context.getClientSecret());
        map.add("scope", context.getScopes());
        map.add("urn:oasis:names:tc:xacml:2.0:subject:role", context.getSubjectRole());
        map.add("urn:oasis:names:tc:xspa:1.0:subject:subject-id", context.getSubjectId());
        map.add("urn:oasis:names:tc:xspa:1.0:subject:organization-id", context.getOrganizationId());
        map.add("urn:oasis:names:tc:xspa:1.0:subject:organization", context.getOrganization());
        map.add("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse", context.getPurposeOfUse());
        map.add("urn:oasis:names:tc:xspa:2.0:subject:npi", context.getNpi());
        return map;
    }

    private HttpHeaders buildHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}
