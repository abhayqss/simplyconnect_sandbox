package com.scnsoft.eldermark.web.controller.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AjaxAccessDeniedHandler extends AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        String ajaxHeader = request.getHeader("X-Requested-With");

        if ("XMLHttpRequest".equals(ajaxHeader)) {
            if (e instanceof MissingCsrfTokenException
                    || e instanceof InvalidCsrfTokenException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            response.getWriter().println("Access is denied.");
        }  else {
            super.handle(request, response, e);
        }
    }
}
