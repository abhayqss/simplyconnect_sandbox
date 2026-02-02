package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.entity.DirectConfiguration;
import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.services.direct.DirectConfigurationService;
import com.scnsoft.eldermark.services.direct.WebServiceClientFactory;
import com.scnsoft.eldermark.shared.DirectConfigurationDto;
import com.scnsoft.eldermark.shared.exceptions.DirectMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DirectConfigurationFacadeImpl implements DirectConfigurationFacade {
    private static final Logger logger = LoggerFactory.getLogger(DirectConfigurationFacadeImpl.class);

    @Autowired
    private DirectConfigurationService directConfigurationService;

    @Autowired
    private WebServiceClientFactory webServiceClientFactory;

    @Override
    public void setPIN(String companyCode, String pin) {
        if (companyCode == null)
            throw new IllegalArgumentException("CompanyCode should not be null.");

        directConfigurationService.setPin(companyCode, pin);
    }

    @Override
    public void uploadKeystore(SaveDocumentCallback callback, String companyCode) {
        if (companyCode == null)
            throw new IllegalArgumentException("CompanyCode should not be null.");

        directConfigurationService.uploadKeystore(callback, companyCode);
    }

    @Override
    public DirectConfigurationDto getDirectConfiguration(String companyCode) {
        if (companyCode == null)
            throw new IllegalArgumentException("CompanyCode should not be null.");

        DirectConfiguration directConfiguration = directConfigurationService.find(companyCode);
        DirectConfigurationDto dto = new DirectConfigurationDto();

        if(directConfiguration == null) {
            return dto;
        }

        dto.setPin(directConfiguration.getPin());
        dto.setKeystoreName(directConfiguration.getKeystoreFile());
        dto.setIsConfigured(directConfiguration.getConfigured());

        return dto;
    }

    @Override
    public boolean verify(String companyCode) throws DirectMessagingException {
        if (companyCode == null)
            throw new IllegalArgumentException("CompanyCode should not be null.");

        try {
            webServiceClientFactory.createRegistrationPort(companyCode);
        } catch (Exception e) {
            logger.error("Config verification failed for " + companyCode, e);
            return false;
        }

        return true;
    }

    @Override
    public boolean isConfigured(String companyCode) throws DirectMessagingException {
        return directConfigurationService.isConfigured(companyCode);
    }
}
