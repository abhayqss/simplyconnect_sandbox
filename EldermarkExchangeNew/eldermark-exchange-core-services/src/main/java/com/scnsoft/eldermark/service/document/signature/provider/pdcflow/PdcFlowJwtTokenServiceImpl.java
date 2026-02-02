package com.scnsoft.eldermark.service.document.signature.provider.pdcflow;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureRequestDao;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

@Service
public class PdcFlowJwtTokenServiceImpl implements PdcFlowJwtTokenService {
    private static final Logger logger = LoggerFactory.getLogger(PdcFlowJwtTokenServiceImpl.class);

    @Value("${application.domain}")
    private String applicationDomain;

    @Value("${pdcflow.api.postback.secret}")
    private String jwtSecret;

    @Autowired
    private DocumentSignatureRequestDao documentSignatureRequestDao;

    @Override
    public String generateToken(Long signatureRequestId, Instant expiresAt) {
        return Jwts.builder()
                .setSubject(Long.toString(signatureRequestId))
                .setIssuedAt(new Date())
                .setIssuer(applicationDomain)
                .setExpiration(Date.from(expiresAt))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();
    }

    @Override
    public boolean validateToken(String token, Long actualDocumentSignatureRequestId) {
        try {
            var jwt = createJwtParser().parseClaimsJws(token);

            var signatureRequestId = Long.valueOf(jwt.getBody().getSubject());

            return signatureRequestId.equals(actualDocumentSignatureRequestId);
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

    private JwtParserBuilder createJwtParserBuilder() {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .requireIssuer(applicationDomain);
    }

    private JwtParser createJwtParser() {
        return createJwtParserBuilder().build();
    }

    interface PdcflowSignatureIdAware {
        BigInteger getPdcflowSignatureId();
    }


}
