package com.scnsoft.eldermark.event.xml.security;

import com.scnsoft.eldermark.event.xml.response.EventResponseStatus;
import com.scnsoft.eldermark.event.xml.response.EventsErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class EventsAccessDeniedEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(EventsAccessDeniedEntryPoint.class);

    private final HttpMessageConverter<Object> xmlHttpMessageConverter;

    @Autowired
    public EventsAccessDeniedEntryPoint(HttpMessageConverter<Object> xmlHttpMessageConverter) {
        this.xmlHttpMessageConverter = xmlHttpMessageConverter;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.warn("User: " + auth.getName() + " attempted to access the protected URL: " + request.getRequestURI());
        }
        var outputMessage = new ServletServerHttpResponse(response);
        outputMessage.setStatusCode(HttpStatus.UNAUTHORIZED);
        xmlHttpMessageConverter.write(new EventsErrorResponse(new EventResponseStatus(102, "Unauthorised Request")), MediaType.APPLICATION_XML, outputMessage);
    }
}
