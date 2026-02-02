package com.scnsoft.eldermark.event.xml.converter;

import com.scnsoft.eldermark.dao.SystemSetupDao;
import com.scnsoft.eldermark.entity.SourceDatabaseAddressAndContacts;
import com.scnsoft.eldermark.entity.SystemSetup;
import com.scnsoft.eldermark.event.xml.schema.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrganizationEntityConverter implements Converter<Organization, com.scnsoft.eldermark.entity.Organization> {

    private final SystemSetupDao systemSetupDao;

    @Autowired
    public OrganizationEntityConverter(SystemSetupDao systemSetupDao) {
        this.systemSetupDao = systemSetupDao;
    }

    @Override
    public com.scnsoft.eldermark.entity.Organization convert(Organization source) {
        var target = new com.scnsoft.eldermark.entity.Organization();
        target.setCreatedAutomatically(true);
        target.setAlternativeId(generateLoginCompanyId(source.getName()));
        target.setMaxDaysToProcessAppointment(3);
        target.setService(Boolean.FALSE);
        target.setName(source.getName());
        target.setEldermark(Boolean.TRUE);
        var addressAndContacts = new SourceDatabaseAddressAndContacts();
        addressAndContacts.setEmail(source.getEmail());
        addressAndContacts.setPhone(source.getPhone());
        target.setAddressAndContacts(addressAndContacts);
        target.setOid(source.getID());
        target.setLastModified(Instant.now());
        convertSystemSetup(target);
        target.setAreReleaseNotesEnabled(true);
        return target;
    }

    private String generateLoginCompanyId(String companyName) {
        String tryCode = companyName.replaceAll("[^A-Za-z]+", "").toUpperCase();
        if (tryCode.length() <= 10) {
            if (!systemSetupDao.existsByLoginCompanyId(tryCode)) return tryCode;
        }
        int commonCodeLength = Math.min(tryCode.length(), 10);
        tryCode = tryCode.substring(0, commonCodeLength);
        if (!systemSetupDao.existsByLoginCompanyId(tryCode)) return tryCode;
        commonCodeLength = Math.min(tryCode.length(), 8);
        int counter = 0;
        while (counter < 100) {
            counter++;
            tryCode = tryCode.substring(0, commonCodeLength) + (counter < 10 ? "0" : "") + counter;
            if (!systemSetupDao.existsByLoginCompanyId(tryCode)) return tryCode;
        }
        throw new RuntimeException("More than 100 Similar oganizations. Organization not created");
    }

    private void convertSystemSetup(com.scnsoft.eldermark.entity.Organization target) {
        if (target.getSystemSetup() == null && target.getAlternativeId() != null) {
            target.setSystemSetup(new SystemSetup());
            target.getSystemSetup().setOrganizationId(target.getId());
        }
        if (target.getSystemSetup() != null) {
            target.getSystemSetup().setLoginCompanyId(target.getAlternativeId());
        }
    }
}
