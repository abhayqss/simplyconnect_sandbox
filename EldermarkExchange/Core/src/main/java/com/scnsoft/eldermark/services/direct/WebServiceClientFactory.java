package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.services.direct.ws.directory.IDirectoryServices;
import com.scnsoft.eldermark.services.direct.ws.mail.SecureIntegrationServiceImap;
import com.scnsoft.eldermark.services.direct.ws.register.RegistrationService;

public interface WebServiceClientFactory {
    public RegistrationService createRegistrationPort(String companyCode);

    public SecureIntegrationServiceImap createMailPort(String companyCode);

    public IDirectoryServices createDirectoryPort(String companyCode);
}