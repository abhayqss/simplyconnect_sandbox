package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.services.direct.ws.mail.SecureIntegrationServiceImap;

public interface WebServiceClientFactory {

    public SecureIntegrationServiceImap createMailPort(String companyCode);

}