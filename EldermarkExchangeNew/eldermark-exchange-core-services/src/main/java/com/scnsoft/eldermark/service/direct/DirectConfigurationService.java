package com.scnsoft.eldermark.service.direct;

import com.scnsoft.eldermark.entity.DirectConfiguration;

import java.util.Optional;

public interface DirectConfigurationService {

    Optional<DirectConfiguration> find(String companyCode);

    String getKeystoreLocation(String companyCode);

    void setConfigured(String companyCode, boolean isConfigured);

}