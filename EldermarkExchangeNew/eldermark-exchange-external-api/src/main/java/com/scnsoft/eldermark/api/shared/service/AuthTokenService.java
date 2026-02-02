package com.scnsoft.eldermark.api.shared.service;

import com.scnsoft.eldermark.api.shared.web.dto.Token;
import com.scnsoft.eldermark.entity.phr.BasePhrExternalAuthToken;

/**
 * @author phomal
 * Created on 1/15/2018.
 */
public interface AuthTokenService {

    boolean validate(String tokenEncoded);

    Token validateTokenOrThrow(String tokenEncoded);

    void expireAllTokens(Long userId);
}
