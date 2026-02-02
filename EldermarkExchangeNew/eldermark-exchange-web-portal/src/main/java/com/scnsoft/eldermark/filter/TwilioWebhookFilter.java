package com.scnsoft.eldermark.filter;

import com.twilio.security.RequestValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TwilioWebhookFilter extends OncePerRequestFilter {

    private final RequestValidator requestValidator;

    public TwilioWebhookFilter(String authToken) {
        requestValidator = new RequestValidator(authToken);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        var pathAndQueryUrl = getRequestUrlAndQueryString(httpServletRequest);
        Map<String, String> postParams = extractPostParams(httpServletRequest);
        String signatureHeader = httpServletRequest.getHeader("X-Twilio-Signature");

        var isValidRequest = requestValidator.validate(pathAndQueryUrl, postParams, signatureHeader);

        if (isValidRequest) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    // Extracts only the POST parameters and converts the parameters Map type
    private Map<String, String> extractPostParams(HttpServletRequest request) {
        String queryString = request.getQueryString();
        var requestParams = request.getParameterMap();
        var queryStringKeys = getQueryStringKeys(queryString);

        return requestParams.entrySet().stream()
                .filter(e -> !queryStringKeys.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }

    private List<String> getQueryStringKeys(String queryString) {
        if (StringUtils.isEmpty(queryString)) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(queryString.split("&"))
                    .map(pair -> pair.split("=")[0])
                    .collect(Collectors.toList());
        }
    }

    // Concatenates the request URL with the query string
    private String getRequestUrlAndQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String requestUrl = request.getRequestURL().toString();
        if (StringUtils.isNotEmpty(queryString)) {
            return requestUrl + "?" + queryString;
        }
        return requestUrl;
    }
}
