package com.scnsoft.eldermark.shared.service;

import com.scnsoft.eldermark.shared.web.entity.Token;

/**
 * @author phomal
 * Created on 1/15/2018.
 */
public interface AuthTokenService {

    boolean validate(String tokenEncoded);

    Token validateTokenOrThrow(String tokenEncoded);

    void expireAllTokens(Long userId);
}
