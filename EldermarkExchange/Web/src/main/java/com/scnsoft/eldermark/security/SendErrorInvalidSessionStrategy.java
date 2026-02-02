package com.scnsoft.eldermark.security;

import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sends {@code statusCode} error when an invalid requested session is detected by the {@code AjaxSessionManagementFilter}.
 */
public class SendErrorInvalidSessionStrategy implements InvalidSessionStrategy {
    private int statusCode = HttpServletResponse.SC_UNAUTHORIZED;

    public SendErrorInvalidSessionStrategy(int statusCode) {
        this.statusCode = statusCode;
    }

    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession();
        response.sendError(statusCode);
    }
}

