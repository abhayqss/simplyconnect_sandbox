package com.scnsoft.eldermark.service.direct;


import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.DirectConfiguration;
import com.scnsoft.eldermark.entity.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@Service
public class DirectConfigurationServiceImpl implements DirectConfigurationService {

    @Autowired
    private OrganizationDao organizationDao;

    @Value("${keystore.upload.basedir}")
    private String keystoreUploadBasedir;

    @Override
    @Transactional(readOnly = true)
    public Optional<DirectConfiguration> find(String companyCode) {
        Organization database = organizationDao.getOrganizationByCompanyId(companyCode);
        return Optional.ofNullable(database).map(Organization::getDirectConfig);
    }

    @Override
    @Transactional(readOnly = true)
    public String getKeystoreLocation(String companyCode) {
        return find(companyCode)
                .map(DirectConfiguration::getKeystoreFile)
                .map(this::buildFileLocation)
                .orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setConfigured(String companyCode, boolean isConfigured) {
        Organization database = organizationDao.getOrganizationByCompanyId(companyCode);

        DirectConfiguration config = database.getDirectConfig();
        if (config != null) {
            config.setConfigured(isConfigured);
            organizationDao.save(database);
        }
    }


    private String buildFileLocation(String fileName) {
        return keystoreUploadBasedir + File.separator + fileName;
    }

}