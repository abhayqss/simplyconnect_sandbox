package com.scnsoft.eldermark.api.shared.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by averazub on 1/3/2017.
 */
public class AuthenticationFilter extends GenericFilterBean {

    private static ObjectMapper mapper = new ObjectMapper();

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws BadCredentialsException, IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)(request);
        HttpServletResponse httpResponse = (HttpServletResponse)(response);

        String token = httpRequest.getHeader("X-Auth-Token");

        try {
            if (token!=null) {
                logger.debug("Trying to authenticate user by X-Auth-Token method. Token: {}", token);
                PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
                Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
                if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
                    throw new BadCredentialsException("Unable to authenticate Domain User for provided credentials");
                }
                logger.debug("User successfully authenticated");
                SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
            }
            logger.debug("AuthenticationFilter is passing request down the filter chain");
            chain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            SecurityContextHolder.clearContext();
            logger.error("Internal authentication service exception", e);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            Response responseJson = Response.errorResponse(PhrExceptionType.UNAUTHORIZED);
            mapper.writeValue(writer, responseJson);
        }
    }

}
