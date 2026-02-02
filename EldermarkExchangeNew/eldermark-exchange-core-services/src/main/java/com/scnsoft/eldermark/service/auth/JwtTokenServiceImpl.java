package com.scnsoft.eldermark.service.auth;

import com.scnsoft.eldermark.beans.UserAuthenticationContext;
import com.scnsoft.eldermark.beans.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenServiceImpl.class);

    private static final String RECORD_SEARCH_FOUND_CLIENT_IDS_CLAIM = "rsci";
    private static final String TWILIO_ACCESSIBLE_ROOM_SID_CLAIM = "twrs";

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${application.domain}")
    private String applicationDomain;


    //Use when needed to generate new secret keys
//    public static void main(String[] args) {
//        System.out.println(Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()));
//    }

    @Override
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getEmployeeId()))
                .setIssuedAt(new Date())
                .setIssuer(applicationDomain)
                .claim(RECORD_SEARCH_FOUND_CLIENT_IDS_CLAIM, userPrincipal.getClientRecordSearchFoundIds())
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();
    }

    @Override
    public Long getUserIdFromJWT(String token) {
        return getUserIdFromJWT(token, false);
    }

    @Override
    public Long getUserIdFromJWT(String token, boolean allowExpiredToken) {
        var claims = parseClaims(token, allowExpiredToken);
        return getEmployeeId(claims);
    }

    @Override
    public UserAuthenticationContext getAuthenticationContextFromJWT(String token) {
        var claims = parseClaims(token, false);

        var ctx = new UserAuthenticationContext();
        ctx.setId(getEmployeeId(claims));

        @SuppressWarnings("unchecked") var clientIds = ((Collection<Number>) claims
                .getOrDefault(RECORD_SEARCH_FOUND_CLIENT_IDS_CLAIM, Collections.emptyList()))
                .stream()
                .map(Number::longValue)
                .collect(Collectors.toSet());

        ctx.setClientRecordSearchFoundIds(clientIds);

        @SuppressWarnings("unchecked") var roomSid = ((String) claims
                .get(TWILIO_ACCESSIBLE_ROOM_SID_CLAIM));
        ctx.setAccessibleRoomSid(roomSid);

        return ctx;
    }

    private Claims parseClaims(String token, boolean allowExpiredToken) {
        JwtParser parser;

        if (allowExpiredToken) {
            parser = createJwtParser(Long.MAX_VALUE / 1000);
        } else {
            parser = createJwtParser();
        }

        return parser.parseClaimsJws(token).getBody();
    }

    private Long getEmployeeId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    @Override
    public boolean validateToken(String authToken) {
        try {
            createJwtParser().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | SecurityException ex) { //SignatureException is deprecated in favor of SecurityException
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        } catch (IncorrectClaimException ex) {
            logger.error("Invalid JWT claim: {}", ex.getMessage());
        }
        return false;
    }

    @Override
    public String generateTokenByRoomSid(Long employeeId, String roomSid) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(employeeId))
                .setIssuedAt(new Date())
                .setIssuer(applicationDomain)
                .claim(TWILIO_ACCESSIBLE_ROOM_SID_CLAIM, roomSid)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();
    }

    private JwtParser createJwtParser() {
        return createJwtParserBuilder().build();
    }

    private JwtParser createJwtParser(long allowedClockSkew) {
        return createJwtParserBuilder()
                .setAllowedClockSkewSeconds(allowedClockSkew)
                .build();
    }

    private JwtParserBuilder createJwtParserBuilder() {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .requireIssuer(applicationDomain);
    }
}
