package com.scnsoft.eldermark.security;

import com.scnsoft.eldermark.dto.security.LoginSso4dDto;
import com.scnsoft.eldermark.dto.sso4d.LoginSso4dResponseDto;
import com.scnsoft.eldermark.service.sso4d.Sso4dService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Sso4DFacadeImpl implements Sso4DFacade {

    @Autowired
    private Sso4dService sso4dService;

    @Override
    public LoginSso4dResponseDto get4dLoginDetails(LoginSso4dDto loginSso4dDto) {
        return sso4dService.get4dLoginDetails(loginSso4dDto.getSubdomain(), loginSso4dDto.getPort(), loginSso4dDto.getSessionId());
    }

}
