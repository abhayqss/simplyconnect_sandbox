package com.scnsoft.eldermark.service.auth;

import com.scnsoft.eldermark.beans.UserAuthenticationContext;
import org.springframework.security.core.Authentication;

public interface JwtTokenService {
    String generateToken(Authentication authentication);

    Long getUserIdFromJWT(String token);

    Long getUserIdFromJWT(String token, boolean allowExpiredToken);

    UserAuthenticationContext getAuthenticationContextFromJWT(String token);

    boolean validateToken(String authToken);

    String generateTokenByRoomSid(Long employeeId, String roomSid);
}
