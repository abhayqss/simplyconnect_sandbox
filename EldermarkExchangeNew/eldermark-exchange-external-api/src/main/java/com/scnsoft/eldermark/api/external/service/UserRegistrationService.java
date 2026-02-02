package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.RegistrationAnswerDto;
import com.scnsoft.eldermark.api.external.web.dto.UserAppDTO;
import com.scnsoft.eldermark.api.shared.web.dto.Token;


public interface UserRegistrationService {

    void updateTimeZone(Long userId, String timeZoneOffset);

    RegistrationAnswerDto signupNew3rdPartyAppUser(String phone, String email, String appName, String appDescription, String timeZoneOffset);

    Token complete(String flowId, String appName);

    UserAppDTO getAppDataBrief(Long id);
}
