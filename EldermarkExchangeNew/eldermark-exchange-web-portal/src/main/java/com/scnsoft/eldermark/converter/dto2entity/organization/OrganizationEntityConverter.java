package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.OrganizationDto;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.SourceDatabaseAddressAndContacts;
import com.scnsoft.eldermark.entity.SystemSetup;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.service.security.OrganizationSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Transactional(readOnly = true)
public class OrganizationEntityConverter implements Converter<OrganizationDto, Organization> {

    private static final boolean DEFAULT_RECEIVE_NON_NETWORK_REFERRALS = false;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private StateService stateService;

    @Autowired
    private Converter<OrganizationDto, SystemSetup> systemSetupConverter;

    @Autowired
    private OrganizationSecurityService organizationSecurityService;

    @Override
    public Organization convert(OrganizationDto source) {
        Organization target;
        if (source.getId() != null) {
            target = organizationService.findById(source.getId());
        } else {
            target = new Organization();
            target.setId(source.getId());
            target.setOid(source.getOid());
            target.setAlternativeId(source.getCompanyId());
            target.setSystemSetup(systemSetupConverter.convert(source));
            target.setChatEnabled(true);
            target.setVideoEnabled(true);
        }

        var targetSourceDatabaseAddressAndContacts = convertSourceDatabaseAddressAndContacts(source, target.getAddressAndContacts());
        target.setAddressAndContacts(targetSourceDatabaseAddressAndContacts);

        target.setMainLogoPath(source.getLogoName());
        target.setService(Boolean.FALSE);
        target.setName(source.getName());
        target.setEldermark(Boolean.TRUE);
        target.setLastModified(Instant.now());
//        target.setCopyEventNotificationsForPatients(source.getCopyEventNotificationsForPatients());

        if (organizationSecurityService.canConfigureMarketplace(source.getId())) {
            target.setReceiveNonNetworkReferrals(source.getAllowExternalInboundReferrals());
        } else if (source.getId() == null) {
            target.setReceiveNonNetworkReferrals(DEFAULT_RECEIVE_NON_NETWORK_REFERRALS);
        }

        target.setAreReleaseNotesEnabled(Boolean.TRUE);
        return target;
    }

    private SourceDatabaseAddressAndContacts convertSourceDatabaseAddressAndContacts(OrganizationDto source, SourceDatabaseAddressAndContacts targetSourceDatabaseAddressAndContacts) {
        if (targetSourceDatabaseAddressAndContacts == null) {
            targetSourceDatabaseAddressAndContacts = new SourceDatabaseAddressAndContacts();
        }
//        targetSourceDatabaseAddressAndContacts.setId(source.getSourceDatabaseAddressAndContactsId());
        targetSourceDatabaseAddressAndContacts.setEmail(source.getEmail());
        targetSourceDatabaseAddressAndContacts.setPhone(source.getPhone());
        targetSourceDatabaseAddressAndContacts.setCity(source.getCity());
        targetSourceDatabaseAddressAndContacts.setPostalCode(source.getZipCode());
        targetSourceDatabaseAddressAndContacts.setStateId(source.getStateId());
        if (source.getStateId() != null) {
            targetSourceDatabaseAddressAndContacts.setState(stateService.findById(source.getStateId()).orElseThrow());
        }
        targetSourceDatabaseAddressAndContacts.setStreetAddress(source.getStreet());
        return targetSourceDatabaseAddressAndContacts;
    }
}
