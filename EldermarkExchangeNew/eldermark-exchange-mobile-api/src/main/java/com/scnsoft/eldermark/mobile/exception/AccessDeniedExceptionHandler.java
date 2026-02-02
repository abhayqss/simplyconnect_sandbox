package com.scnsoft.eldermark.mobile.exception;

import com.scnsoft.eldermark.exception.AuthException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedExceptionHandler.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        logger.error("Responding with 403 forbidden error. Message - {}", e.getMessage());
        handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, new AuthException(InternalServerExceptionType.AUTH_FORBIDDEN));
    }
}
