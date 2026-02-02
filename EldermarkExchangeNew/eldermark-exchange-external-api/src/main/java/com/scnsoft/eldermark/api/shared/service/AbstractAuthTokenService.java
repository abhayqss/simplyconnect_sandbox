package com.scnsoft.eldermark.api.shared.service;

import com.scnsoft.eldermark.entity.phr.BasePhrExternalAuthToken;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.web.dto.Token;
import com.scnsoft.eldermark.api.shared.web.security.SymmetricKeyPasswordEncoder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

/**
 * @author phomal
 * Created on 1/19/2018.
 */
public abstract class AbstractAuthTokenService<T extends BasePhrExternalAuthToken, R extends JpaRepository<T, Long>> implements AuthTokenService {

    protected final SymmetricKeyPasswordEncoder passwordEncoder;
    protected final R authTokenDao;

    protected AbstractAuthTokenService(SymmetricKeyPasswordEncoder passwordEncoder, R authTokenDao) {
        this.passwordEncoder = passwordEncoder;
        this.authTokenDao = authTokenDao;
    }

    protected abstract Iterable<T> getKnownTokensForUser(Long userId);

    @Override
    public boolean validate(String tokenEncoded) {
        try {
            Token token = Token.fromEncodedJsonString(tokenEncoded);
            var authTokens = getKnownTokensForUser(token.getUserId());
            for (var authToken : authTokens) {
                if (token.matches(authToken.getTokenEncoded(), passwordEncoder)) return true;
            }
            return false;
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            return false;
        }
    }

    // intentionally non-transactional
    public Token validateTokenOrThrow(String tokenEncoded) {
        if (!validate(tokenEncoded)) {
            throw new PhrException(PhrExceptionType.INVALID_TOKEN);
        }
        return Token.fromEncodedJsonString(tokenEncoded);
    }

    @Override
    public void expireAllTokens(Long userId) {
       var tokens = getKnownTokensForUser(userId);
       var current = Instant.now();
        for (var authToken : tokens) {
            authToken.setExpirationTime(current);
        }
        authTokenDao.saveAll(tokens);
    }
}
