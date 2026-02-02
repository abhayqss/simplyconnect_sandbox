package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.entity.RegistrationApplication;

public interface UserRegistrationApplicationService {

    RegistrationApplication getOrCreateRegistrationApplicationFor3rdPartyApp(String phone, String email, String appName);

    RegistrationApplication save(RegistrationApplication application);

    RegistrationApplication getRegistrationApplication(String flowId, String appName, RegistrationApplication.Step step);

    void completeRegistration(RegistrationApplication application);
}
