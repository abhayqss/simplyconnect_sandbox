package com.scnsoft.service.security;

import com.scnsoft.eldermark.dao.phr.AuthTokenDao;
import com.scnsoft.eldermark.entity.phr.AuthToken;
import com.scnsoft.eldermark.shared.service.AbstractAuthTokenService;
import com.scnsoft.eldermark.shared.web.security.SymmetricKeyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mtsylko
 * Created on 6/20/2018.
 */
@Service
public class ApiAuthTokenService extends AbstractAuthTokenService {

    private final AuthTokenDao authTokenDao;

    @Autowired
    public ApiAuthTokenService(AuthTokenDao authTokenDao, SymmetricKeyPasswordEncoder passwordEncoder) {
        super(passwordEncoder, authTokenDao);
        this.authTokenDao = authTokenDao;
    }

    @Override
    protected Iterable<AuthToken> getKnownTokensForUser(Long userId) {
        return authTokenDao.findActiveByThirdPartyAppId(userId);
    }


}
