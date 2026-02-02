package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.dao.phr.AuthTokenDao;
import com.scnsoft.eldermark.entity.phr.AuthToken;
import com.scnsoft.eldermark.entity.phr.ThirdPartyApplication;
import com.scnsoft.eldermark.shared.service.AbstractAuthTokenService;
import com.scnsoft.eldermark.shared.web.entity.Token;
import com.scnsoft.eldermark.shared.web.security.SymmetricKeyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author phomal
 * Created on 1/15/2018.
 */
@Service
public class ApiAuthTokenService extends AbstractAuthTokenService {

    @Autowired
    public ApiAuthTokenService(AuthTokenDao authTokenDao, SymmetricKeyPasswordEncoder passwordEncoder) {
        super(passwordEncoder, authTokenDao);
    }

    @Override
    protected Iterable<AuthToken> getKnownTokensForUser(Long userId) {
        return authTokenDao.findActiveByThirdPartyAppId(userId);
    }

    public Token generateFor(ThirdPartyApplication userApp) {
        Token token = Token.generateToken(userApp.getId());
        AuthToken authToken = new AuthToken();
        authToken.setTokenEncoded(Token.encode(token, passwordEncoder));
        authToken.setUserApplication(userApp);
        authToken.setIssuedAt(new Date());
        authTokenDao.save(authToken);

        return token;
    }

}
