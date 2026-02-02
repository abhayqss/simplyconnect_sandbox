package com.scnsoft.eldermark.api.shared.web.security;

import com.scnsoft.eldermark.api.shared.service.AuthTokenService;
import com.scnsoft.eldermark.api.shared.web.dto.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 1/3/2017.
 */
@Component
public class TokenAuthenticationManager implements AuthenticationManager {

    @Autowired
    AuthTokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            PreAuthenticatedAuthenticationToken authenticationToken = (PreAuthenticatedAuthenticationToken) authentication;
            String tokenStr = (String) authenticationToken.getPrincipal();
            if (!tokenService.validate(tokenStr)) return authenticationToken;
            final Token token = Token.fromEncodedJsonString(tokenStr);
            authenticationToken.setDetails(token);
            authenticationToken.setAuthenticated(true);
            return authenticationToken;
        } catch (Exception e) {
            return null;
        }
    }
}
