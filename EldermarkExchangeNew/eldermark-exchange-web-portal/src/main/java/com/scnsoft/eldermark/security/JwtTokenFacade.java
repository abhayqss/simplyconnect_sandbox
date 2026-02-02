package com.scnsoft.eldermark.security;

import com.scnsoft.eldermark.beans.UserAuthenticationContext;
import com.scnsoft.eldermark.service.auth.JwtTokenService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFacade {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFacade.class);

    public static final String JWT_HEADER_AND_PAYLOAD_COOKIE = "jwtHeaderAndPayload";
    public static final String JWT_SIGNATURE_COOKIE = "jwtSignature";
    private static final String AUTH_WITH_COOKIE_HEADER = "X-Auth-With-Cookies";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String AUTH_WITH_COOKIE_NO_UPDATE = "no-update";

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private LoggedUserService loggedUserService;

    public String generateToken(Authentication authentication) {
        return jwtTokenService.generateToken(authentication);
    }

    public Long getUserIdFromJWT(String token) {
        return jwtTokenService.getUserIdFromJWT(token);
    }

    public Long getUserIdFromJWT(String token, boolean allowExpiredToken) {
        return jwtTokenService.getUserIdFromJWT(token, allowExpiredToken);
    }

    public UserAuthenticationContext getAuthenticationContextFromJWT(String token) {
        return jwtTokenService.getAuthenticationContextFromJWT(token);
    }

    public boolean validateToken(String authToken) {
        return jwtTokenService.validateToken(authToken);
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        if (isAuthWithCookies(request)) {
            return readJwtFromCookies(request);
        }
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    public void updateTokenInCookie(HttpServletRequest request, HttpServletResponse response) {
        if (!isAuthWithCookies(request) ||
                isAuthWithCookiesNoUpdate(request) ||
                SecurityContextHolder.getContext().getAuthentication() == null ||
                loggedUserService.getCurrentUser().isEmpty()) {
            return;
        }
        var token = generateToken(SecurityContextHolder.getContext().getAuthentication());
        writeTokenInCookie(response, token);
    }

    public void setTokenInCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        if (!isAuthWithCookies(request) || SecurityContextHolder.getContext().getAuthentication() == null) {
            return;
        }
        writeTokenInCookie(response, token);
    }

    public void removeTokenFromCookie(HttpServletRequest request, HttpServletResponse response) {
        if (!isAuthWithCookies(request)) {
            return;
        }
        var cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_HEADER_AND_PAYLOAD_COOKIE.equals(cookie.getName()) ||
                        JWT_SIGNATURE_COOKIE.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    // TODO remove
    @SuppressFBWarnings("HTTPONLY_COOKIE")
    private void writeTokenInCookie(HttpServletResponse response, String token) {
        var dot = token.lastIndexOf('.');
        String headerAndPayload = token.substring(0, dot);
        String signature = token.substring(dot);

        var headerAndPayloadCookie = new Cookie(JWT_HEADER_AND_PAYLOAD_COOKIE, headerAndPayload);
        headerAndPayloadCookie.setSecure(true);
        // headerAndPayloadCookie.setHttpOnly(true);
        headerAndPayloadCookie.setMaxAge(jwtExpirationInMs / 1000);
        headerAndPayloadCookie.setPath("/");

        var signatureCookie = new Cookie(JWT_SIGNATURE_COOKIE, signature);
        signatureCookie.setSecure(true);
        signatureCookie.setHttpOnly(true);
        signatureCookie.setMaxAge(jwtExpirationInMs / 1000);
        signatureCookie.setPath("/");

        response.addCookie(headerAndPayloadCookie);
        response.addCookie(signatureCookie);
    }

    private String readJwtFromCookies(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String headerAndPayload = null;
        String signature = null;
        for (var cookie : cookies) {
            if (JWT_HEADER_AND_PAYLOAD_COOKIE.equals(cookie.getName())) {
                headerAndPayload = cookie.getValue();
            }
            if (JWT_SIGNATURE_COOKIE.equals(cookie.getName())) {
                signature = cookie.getValue();
            }
        }
        if (StringUtils.isNoneBlank(headerAndPayload, signature)) {
            return headerAndPayload + signature;
        }
        return null;
    }

    public boolean isAuthWithCookies(HttpServletRequest request) {
        return request.getHeader(AUTH_WITH_COOKIE_HEADER) != null;
    }

    public boolean isAuthWithCookiesNoUpdate(HttpServletRequest request) {
        return AUTH_WITH_COOKIE_NO_UPDATE.equals(request.getHeader(AUTH_WITH_COOKIE_HEADER));
    }

}
