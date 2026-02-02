package com.scnsoft.eldermark.api.external.service.security;

import com.scnsoft.eldermark.api.external.dao.ExternalAuthTokenDao;
import com.scnsoft.eldermark.api.external.entity.ExternalAuthToken;
import com.scnsoft.eldermark.api.external.entity.ThirdPartyApplication;
import com.scnsoft.eldermark.api.shared.service.AbstractAuthTokenService;
import com.scnsoft.eldermark.api.shared.web.dto.Token;
import com.scnsoft.eldermark.api.shared.web.security.SymmetricKeyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author phomal
 * Created on 1/15/2018.
 */
@Service
public class ApiAuthTokenServiceImpl extends AbstractAuthTokenService<ExternalAuthToken, ExternalAuthTokenDao>
        implements ApiAuthTokenService {

    @Autowired
    public ApiAuthTokenServiceImpl(SymmetricKeyPasswordEncoder passwordEncoder, ExternalAuthTokenDao authTokenDao) {
        super(passwordEncoder, authTokenDao);
    }

    @Override
    protected Iterable<ExternalAuthToken> getKnownTokensForUser(Long userId) {
        return authTokenDao.findActiveByThirdPartyAppId(userId);
    }

    @Override
    public Token generateFor(ThirdPartyApplication userApp) {
        Token token = Token.generateToken(userApp.getId());
        var authToken = new ExternalAuthToken();
        authToken.setTokenEncoded(Token.encode(token, passwordEncoder));
        authToken.setUserApplication(userApp);
        authToken.setIssuedAt(Instant.now());
        authTokenDao.save(authToken);

        return token;
    }

}
