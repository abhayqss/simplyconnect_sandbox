package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.RegistrationApplicationDao;
import com.scnsoft.eldermark.api.external.dao.ThirdPartyApplicationDao;
import com.scnsoft.eldermark.api.external.entity.RegistrationApplication;
import com.scnsoft.eldermark.api.external.entity.RegistrationStep;
import com.scnsoft.eldermark.api.external.entity.ThirdPartyApplication;
import com.scnsoft.eldermark.api.shared.service.RegistrationStepService;
import com.scnsoft.eldermark.util.Normalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserRegistrationApplicationServiceImpl implements UserRegistrationApplicationService {

    @Autowired
    private RegistrationApplicationDao registrationApplicationDao;

    @Autowired
    private ThirdPartyApplicationDao thirdPartyApplicationDao;

    @Autowired
    RegistrationStepService registrationStepService;

    @Override
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
        final Pageable top1 = PageRequest.of(0, 1, Sort.Direction.DESC, "currentSignupTime");
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

    @Override
    @Transactional
    public RegistrationApplication save(RegistrationApplication application) {
        return registrationApplicationDao.save(application);
    }

    @Override
    public RegistrationApplication getRegistrationApplication(String flowId, String appName, RegistrationApplication.Step step) {
        RegistrationApplication.Type type = RegistrationApplication.Type.APPLICATION;
        final RegistrationStep registrationStep = registrationStepService.convert(step);

        return registrationApplicationDao.findOneByFlowIdAndFirstNameAndRegistrationStepAndRegistrationTypeOrderByCurrentSignupTimeDesc(
                flowId, appName, registrationStep, type);
    }

    @Override
    public void completeRegistration(RegistrationApplication application) {
        application.setLastSignupTime(new Date());
        application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.COMPLETED));
    }

}
