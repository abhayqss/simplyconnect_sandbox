package com.scnsoft.eldermark.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ExchangeExceptionMappingAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    @Autowired
    private ExtraParamAuthenticationFilter usernamePasswordAuthenticationFilter;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String usernameParameter = usernamePasswordAuthenticationFilter.getUsernameParameter();
        String orgParameter = usernamePasswordAuthenticationFilter.getExtraParameter();
        String lastUserName = request.getParameter(usernameParameter);
        String lastOrg = request.getParameter(orgParameter);
        HttpSession session = request.getSession(false);
        if (session != null || isAllowSessionCreation()) {
            request.getSession().setAttribute("last_username", lastUserName);
            request.getSession().setAttribute("last_company", lastOrg);
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
