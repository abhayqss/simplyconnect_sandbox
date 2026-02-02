package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.RegistrationApplicationDao;
import com.scnsoft.eldermark.dao.phr.ThirdPartyApplicationDao;
import com.scnsoft.eldermark.entity.phr.RegistrationApplication;
import com.scnsoft.eldermark.entity.phr.RegistrationStep;
import com.scnsoft.eldermark.entity.phr.ThirdPartyApplication;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import com.scnsoft.eldermark.shared.service.RegistrationStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 1/15/2018.
 */
@Service
public class UserRegistrationApplicationService {

    @Autowired
    RegistrationApplicationDao registrationApplicationDao;

    @Autowired
    ThirdPartyApplicationDao thirdPartyApplicationDao;

    @Autowired
    RegistrationStepService registrationStepService;

    public RegistrationApplication getOrCreateRegistrationApplicationFor3rdPartyApp(String phone, String email, String appName) {
        RegistrationApplication.Type type = RegistrationApplication.Type.APPLICATION;
        if (phone == null) phone = "";
        if (email == null) email = "";

        RegistrationApplication application = getExistingApplication(phone, email, appName, type);
        if (application == null) {
            ThirdPartyApplication userApp = thirdPartyApplicationDao.findByName(appName);
            application = createNewApplication(userApp, phone, email, appName, type);
        }

        initApplication(application);

        return application;
    }

    private void initApplication(RegistrationApplication application) {
        application.setSignupAttemptCount(application.getSignupAttemptCount() + 1);
        application.setCurrentSignupTime(new Date());
        // the next step after sign up is verification
        application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.CONFIRMATION));
    }

    private RegistrationApplication getExistingApplication(String phone, String email, String appName, RegistrationApplication.Type type) {
        final Pageable top1 = new PageRequest(0, 1, Sort.Direction.DESC, "currentSignupTime");
        final List<RegistrationApplication> applications = registrationApplicationDao.findAllBy(
                Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone), appName, null, type,
                registrationStepService.excludeCompleted(), top1);
        return CollectionUtils.isEmpty(applications) ? null : applications.get(0);
    }

    private static RegistrationApplication createNewApplication(ThirdPartyApplication userApp, String phone, String email, String appName,
                                                                RegistrationApplication.Type type) {
        RegistrationApplication application = new RegistrationApplication();
        application.setPhone(phone);
        application.setEmail(email);
        application.setFirstName(appName);
        if (userApp != null) {
            application.setThirdPartyApplication(userApp);
            application.setTimeZoneOffset(userApp.getTimeZoneOffset());
        }

        application.setRegistrationType(type);
        application.setSignupAttemptCount(0);
        application.setPhoneConfirmationAttemptCount(0);

        return application;
    }

    @Transactional
    public RegistrationApplication save(RegistrationApplication application) {
        return registrationApplicationDao.save(application);
    }

    public RegistrationApplication getRegistrationApplication(String flowId, String appName, RegistrationApplication.Step step) {
        RegistrationApplication.Type type = RegistrationApplication.Type.APPLICATION;
        final RegistrationStep registrationStep = registrationStepService.convert(step);

        return registrationApplicationDao.findOneByFlowIdAndFirstNameAndRegistrationStepAndRegistrationTypeOrderByCurrentSignupTimeDesc(
                flowId, appName, registrationStep, type);
    }

    public void completeRegistration(RegistrationApplication application) {
        application.setLastSignupTime(new Date());
        application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.COMPLETED));
    }

}
