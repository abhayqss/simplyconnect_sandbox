package com.scnsoft.eldermark.security;

import com.scnsoft.eldermark.dto.security.LoginSso4dDto;
import com.scnsoft.eldermark.dto.sso4d.LoginSso4dResponseDto;

public interface Sso4DFacade {
    LoginSso4dResponseDto get4dLoginDetails(LoginSso4dDto loginSso4dDto);
}
