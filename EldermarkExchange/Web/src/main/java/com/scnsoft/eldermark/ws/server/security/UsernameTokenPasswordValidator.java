package com.scnsoft.eldermark.ws.server.security;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.UsernameToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class UsernameTokenPasswordValidator extends org.apache.ws.security.validate.UsernameTokenValidator {

    private AuthenticationManager authenticationManager;

    @Override
    protected void verifyPlaintextPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {
        try {
            Authentication authResult = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    usernameToken.getName(), usernameToken.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authResult);

        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();

            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION, null, null, failed);
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
