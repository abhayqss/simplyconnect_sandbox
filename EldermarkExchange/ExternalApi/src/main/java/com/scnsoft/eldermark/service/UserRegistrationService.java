package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.ThirdPartyApplicationDao;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.RegistrationApplication;
import com.scnsoft.eldermark.entity.phr.ThirdPartyApplication;
import com.scnsoft.eldermark.service.security.ApiAuthTokenService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.web.entity.Token;
import com.scnsoft.eldermark.web.entity.RegistrationAnswerDto;
import com.scnsoft.eldermark.web.entity.UserAppDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

/**
 * @author phomal
 * Created by phomal on 1/15/2018.
 */
@Service
@Transactional
public class UserRegistrationService {

    @Autowired
    ThirdPartyApplicationDao thirdPartyApplicationDao;

    @Autowired
    ApiAuthTokenService authTokenService;

    @Autowired
    UserRegistrationApplicationService userRegistrationApplicationService;

    Logger logger = Logger.getLogger(UserRegistrationService.class.getName());

    public void updateTimeZone(Long userId, String timeZoneOffset) {
        if (userId != null && timeZoneOffset != null) {
            thirdPartyApplicationDao.updateTimezone(userId, Long.valueOf(timeZoneOffset));
        }
    }

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

    @Transactional(readOnly = true)
    public UserAppDTO getAppDataBrief(Long id) {
        UserAppDTO userDTO = new UserAppDTO();
        ThirdPartyApplication userApp = thirdPartyApplicationDao.findOne(id);
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
