package com.scnsoft.eldermark.shared.service;

import com.scnsoft.eldermark.dao.phr.AuthTokenDao;
import com.scnsoft.eldermark.entity.phr.AuthToken;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.web.entity.Token;
import com.scnsoft.eldermark.shared.web.security.SymmetricKeyPasswordEncoder;

import java.util.Date;

/**
 * @author phomal
 * Created on 1/19/2018.
 */
public abstract class AbstractAuthTokenService implements AuthTokenService {

    protected final SymmetricKeyPasswordEncoder passwordEncoder;

    protected final AuthTokenDao authTokenDao;

    protected AbstractAuthTokenService(SymmetricKeyPasswordEncoder passwordEncoder, AuthTokenDao authTokenDao) {
        this.passwordEncoder = passwordEncoder;
        this.authTokenDao = authTokenDao;
    }

    protected abstract Iterable<AuthToken> getKnownTokensForUser(Long userId);

    @Override
    public boolean validate(String tokenEncoded) {
        try {
            Token token = Token.fromEncodedJsonString(tokenEncoded);
            Iterable<AuthToken> authTokens = getKnownTokensForUser(token.getUserId());
            for (AuthToken authToken : authTokens) {
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
        final Iterable<AuthToken> tokens = getKnownTokensForUser(userId);
        final Date current = new Date();
        for (AuthToken authToken : tokens) {
            authToken.setExpirationTime(current);
        }
        authTokenDao.save(tokens);
    }
}
