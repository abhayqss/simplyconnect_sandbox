package com.scnsoft.eldermark.api.external.service.security;

import com.scnsoft.eldermark.api.external.entity.ThirdPartyApplication;
import com.scnsoft.eldermark.api.shared.service.AuthTokenService;
import com.scnsoft.eldermark.api.shared.web.dto.Token;

public interface ApiAuthTokenService extends AuthTokenService {

    Token generateFor(ThirdPartyApplication userApp);
}
