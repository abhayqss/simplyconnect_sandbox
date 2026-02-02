package com.scnsoft.eldermark.service.sso4d;

import com.scnsoft.eldermark.dto.sso4d.LoginSso4dResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;

@Service
public class Sso4dServiceImpl implements Sso4dService {

    private static final Logger logger = LoggerFactory.getLogger(Sso4dServiceImpl.class);

    @Value("${sso.4d.domain}")
    private String domain;
    @Value("${sso.4d.login.endpoint}")
    private String authorizationRestEndpoint;
    @Value("${sso.4d.validate.endpoint}")
    private String validationSsoEndpoint;
    @Value("${sso.4d.username}")
    private String username;
    @Value("${sso.4d.password}")
    private String password;

    private final static String AUTH_COOKIE_NAME = "WASID4D";

    @Autowired
    @Qualifier("jsonRestTemplateBuilder")
    private RestTemplateBuilder restTemplateBuilder;

    @Override
    public LoginSso4dResponseDto get4dLoginDetails(String subdomain, String port, String sessionId) {
        var authCookie = authorizeTo4dRestServicesAndGetAuthCookie(subdomain, port);
        var entity = prepareValidateRequest(authCookie);
        var urlWithParam = constructValidateUrl(subdomain, port, sessionId);
        ResponseEntity<LoginSso4dResponseDto> stringResponseEntity = restTemplateBuilder.build().exchange(urlWithParam, HttpMethod.GET, entity, LoginSso4dResponseDto.class);
        return stringResponseEntity.getBody();
    }

    private String authorizeTo4dRestServicesAndGetAuthCookie(String subdomain, String port) {
        var entity = prepareAuthRestRequest();
        var url = constructAuthRestUrl(subdomain, port);
        ResponseEntity<Object> response = restTemplateBuilder.build().postForEntity(url, entity, Object.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.info("Login request to 4D failed. Status: {} ", response.getStatusCode().value());
            return null;
        }
        HttpHeaders headers = response.getHeaders();
        String cookies =  headers.getFirst(HttpHeaders.SET_COOKIE);
        return HttpCookie.parse(cookies).stream().filter(httpCookie -> AUTH_COOKIE_NAME.equals(httpCookie.getName())).map(HttpCookie::toString).findFirst().orElse(null);
    }

    private String constructAuthRestUrl(String subdomain, String port) {
        StringBuilder sb = new StringBuilder(constructDomainUrl(subdomain, port));
        sb.append(authorizationRestEndpoint);
        return sb.toString();
    }

    private String constructValidateUrl(String subdomain, String port, String sessionId) {
        StringBuilder sb = new StringBuilder(constructDomainUrl(subdomain, port));
        sb.append(validationSsoEndpoint);
        sb.append("\"sessionID='");
        sb.append(sessionId);
        sb.append("'\"");
        return sb.toString();
    }

    private String constructDomainUrl(String subdomain, String port) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(subdomain);
        sb.append(".");
        sb.append(domain);
        sb.append(":");
        sb.append(port);
        return sb.toString();
    }

    private HttpEntity<String> prepareAuthRestRequest() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("username-4D", username);
        headers.add("password-4D", password);

        return new HttpEntity<>(headers);
    }

    private HttpEntity<String> prepareValidateRequest(String authCookie) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", authCookie);
        return new HttpEntity<>(headers);
    }
}
