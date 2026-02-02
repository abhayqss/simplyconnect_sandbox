package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.ThirdPartyApplicationDao;
import com.scnsoft.eldermark.api.external.entity.RegistrationApplication;
import com.scnsoft.eldermark.api.external.entity.ThirdPartyApplication;
import com.scnsoft.eldermark.api.external.service.security.ApiAuthTokenService;
import com.scnsoft.eldermark.api.external.web.dto.RegistrationAnswerDto;
import com.scnsoft.eldermark.api.external.web.dto.UserAppDTO;
import com.scnsoft.eldermark.api.shared.entity.AccountType;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.web.dto.Token;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    private ThirdPartyApplicationDao thirdPartyApplicationDao;

    @Autowired
    private ApiAuthTokenService authTokenService;

    @Autowired
    private UserRegistrationApplicationService userRegistrationApplicationService;

    @Override
    public void updateTimeZone(Long userId, String timeZoneOffset) {
        if (userId != null && timeZoneOffset != null) {
            thirdPartyApplicationDao.updateTimezone(userId, Long.valueOf(timeZoneOffset));
        }
    }

    @Override
    public RegistrationAnswerDto signupNew3rdPartyAppUser(String phone, String email, String appName, String appDescription, String timeZoneOffset) {
        appName = StringUtils.trim(appName);
        RegistrationApplication application = userRegistrationApplicationService.getOrCreateRegistrationApplicationFor3rdPartyApp(
                phone, email, appName);
        application.setAppDescription(appDescription);
        application.setTimeZoneOffset(Integer.valueOf(timeZoneOffset));

        // Create user registration application in DB
        application = userRegistrationApplicationService.save(application);

        return transform(application);
    }

    @Override
    public Token complete(String flowId, String appName) {
        appName = StringUtils.trim(appName);
        RegistrationApplication application = userRegistrationApplicationService.getRegistrationApplication(flowId, appName,
                RegistrationApplication.Step.COMPLETION);
        if (application == null || application.getThirdPartyApplication() == null) {
            throw new PhrException(PhrExceptionType.REGISTRATION_FLOW_NOT_FOUND);
        }

        ThirdPartyApplication userApp = application.getThirdPartyApplication();
        Token token = authTokenService.generateFor(userApp);

        userRegistrationApplicationService.completeRegistration(application);
        userRegistrationApplicationService.save(application);

        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public UserAppDTO getAppDataBrief(Long id) {
        UserAppDTO userDTO = new UserAppDTO();
        ThirdPartyApplication userApp = thirdPartyApplicationDao.findById(id).orElseThrow();
        userDTO.setType(AccountType.Type.APPLICATION.toString());
        userDTO.setAppName(userApp.getName());
        userDTO.setUserId(id);

        return userDTO;
    }

    private static RegistrationAnswerDto transform(RegistrationApplication application) {
        RegistrationAnswerDto dto = new RegistrationAnswerDto();
        dto.setFlowId(application.getFlowId());
        dto.setComments("Your application is accepted for review. Remember the returned `flowId` and your `appName`, you'll need these two parameters to complete the registration.");
        return dto;
    }

}
