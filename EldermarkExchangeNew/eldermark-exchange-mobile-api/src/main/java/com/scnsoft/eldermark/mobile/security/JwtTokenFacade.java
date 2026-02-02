package com.scnsoft.eldermark.mobile.security;

import com.scnsoft.eldermark.beans.UserAuthenticationContext;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface JwtTokenFacade {
    String generateToken(Authentication authentication);

    Long getUserIdFromJWT(String token);

    Long getUserIdFromJWT(String token, boolean allowExpiredToken);

    boolean validateToken(String authToken);

    String getJwtFromRequest(HttpServletRequest request);

    String generateTokenByRoomSid(Long employeeId, String roomSid);

    UserAuthenticationContext getAuthenticationContextFromJWT(String token);
}
