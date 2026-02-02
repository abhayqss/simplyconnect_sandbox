package com.scnsoft.eldermark.mobile.security;

import com.scnsoft.eldermark.beans.UserAuthenticationContext;
import com.scnsoft.eldermark.service.auth.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class JwtTokenFacadeImpl implements JwtTokenFacade {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public String generateToken(Authentication authentication) {
        return jwtTokenService.generateToken(authentication);
    }

    @Override
    public Long getUserIdFromJWT(String token) {
        return jwtTokenService.getUserIdFromJWT(token);
    }

    @Override
    public Long getUserIdFromJWT(String token, boolean allowExpiredToken) {
        return jwtTokenService.getUserIdFromJWT(token, allowExpiredToken);
    }

    @Override
    public boolean validateToken(String authToken) {
        return jwtTokenService.validateToken(authToken);
    }

    @Override
    public String getJwtFromRequest(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    @Override
    public String generateTokenByRoomSid(Long employeeId, String roomSid) {
        return jwtTokenService.generateTokenByRoomSid(employeeId, roomSid);
    }

    @Override
    public UserAuthenticationContext getAuthenticationContextFromJWT(String token) {
        return jwtTokenService.getAuthenticationContextFromJWT(token);
    }
}
