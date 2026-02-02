package com.scnsoft.eldermark.web.interceptor.hashkey;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InvalidHashKeyStrategyImpl implements InvalidHashKeyStrategy {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String hashKeyFailureUrl;
    private int statusCode = HttpServletResponse.SC_BAD_REQUEST;

    public InvalidHashKeyStrategyImpl(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void onInvalidHashKeyDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // handle AJAX request
        String ajaxHeader = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(ajaxHeader)) {
            response.sendError(statusCode);
            return;
        }

        // handle non-AJAX request
        if (hashKeyFailureUrl != null) {
            redirectStrategy.sendRedirect(request, response, hashKeyFailureUrl);
        } else {
            response.sendError(statusCode);
        }
    }

    public void setHashKeyFailureUrl(String hashKeyFailureUrl) {
        this.hashKeyFailureUrl = hashKeyFailureUrl;
    }
}