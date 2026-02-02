package com.scnsoft.eldermark.web.security;

import com.scnsoft.eldermark.dao.phr.AuthTokenDao;
import com.scnsoft.eldermark.entity.phr.AuthToken;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.service.AbstractAuthTokenService;
import com.scnsoft.eldermark.shared.web.entity.Token;
import com.scnsoft.eldermark.shared.web.security.SymmetricKeyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author phomal
 * Created on 1/18/2018.
 */
@Service
public class PhrAuthTokenService extends AbstractAuthTokenService {

    @Autowired
    public PhrAuthTokenService(AuthTokenDao authTokenDao, SymmetricKeyPasswordEncoder passwordEncoder) {
        super(passwordEncoder, authTokenDao);
    }

    @Override
    protected Iterable<AuthToken> getKnownTokensForUser(Long userId) {
        return authTokenDao.findActiveByUserMobileId(userId);
    }

    public Token generateFor(User user) {
        Token token = Token.generateToken(user.getId());
        AuthToken authToken = new AuthToken();
        authToken.setUserMobile(user);
        authToken.setTokenEncoded(Token.encode(token, passwordEncoder));
        authToken.setIssuedAt(new Date());
        authTokenDao.save(authToken);

        return token;
    }

}
