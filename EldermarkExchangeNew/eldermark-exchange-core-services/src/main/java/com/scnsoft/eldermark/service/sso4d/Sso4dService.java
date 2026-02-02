package com.scnsoft.eldermark.service.sso4d;

import com.scnsoft.eldermark.dto.sso4d.LoginSso4dResponseDto;

public interface Sso4dService {
    LoginSso4dResponseDto get4dLoginDetails(String subdomain, String port, String sessionId);
}
