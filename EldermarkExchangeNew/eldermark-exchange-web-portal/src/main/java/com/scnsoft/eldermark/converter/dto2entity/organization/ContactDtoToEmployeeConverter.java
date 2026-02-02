package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dto.ContactDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactDtoToEmployeeConverter implements Converter<ContactDto, Employee> {

    private final String DEFAULT_PASSWORD = "password";

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private Converter<ContactDto, Person> contactPersonEntityConverter;

    @SuppressFBWarnings(value = "HARD_CODE_PASSWORD", justification = "It is not possible to login with this password")
    @Override
    public Employee convert(ContactDto source) {
        Employee target = null;
        if (source.getId() == null) {
            target = new Employee();
            var org = organizationDao.findById(source.getOrganizationId()).orElseThrow();
            target.setOrganization(org);
            target.setOrganizationId(source.getOrganizationId());
            target.setLoginName(source.getLogin());
            target.setPassword(DEFAULT_PASSWORD);
            target.setCompany(org.getSystemSetup().getLoginCompanyId());
            target.setStatus(EmployeeStatus.PENDING);
            CareCoordinationConstants.setLegacyId(target);
            target.setCreatedAutomatically(false);
        } else {
            target = employeeDao.findById(source.getId()).orElseThrow();
            if (!EmployeeStatus.ACTIVE.equals(target.getStatus()) && source.getEnableContact()) {
                target.setManualActivationDateTime(Instant.now());
            }
            if (target.getStatus().equals(EmployeeStatus.ACTIVE) || target.getStatus().equals(EmployeeStatus.INACTIVE)) {
                target.setStatus(BooleanUtils.isTrue(source.getEnableContact()) ? EmployeeStatus.ACTIVE : EmployeeStatus.INACTIVE);
            }
        }
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setCommunity(communityDao.findById(source.getCommunityId()).orElseThrow());

        target.setSecureMessaging(source.getSecureMail());
        target.setSecureMessagingActive(StringUtils.isNotEmpty(source.getSecureMail()));


        target.setCareTeamRole(careTeamRoleDao.findById(source.getSystemRoleId()).orElseThrow());

        target.setPerson(contactPersonEntityConverter.convert(source));
        target.setIsCommunityAddressUsed(source.getIsCommunityAddressUsed());

        if (source.getAvatar() != null && source.getAvatarId() != null) {
            target.getAvatar().setId(source.getAvatarId());
        } else {
            target.setMultipartFile(source.getAvatar());
        }
        target.setShouldRemoveAvatar(source.getShouldRemoveAvatar());

        target.setCreator(loggedUserService.getCurrentEmployee());

        target.setQaIncidentReports(source.isQaIncidentReports());
        if (target.getCareTeamRole().getCode().equals(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES)) {
            //todo - what to do association breaks and added as client to chat?
            if (target.getAssociatedClients() != null) {
                target.getAssociatedClients().clear();
            } else {
                target.setAssociatedClients(new ArrayList<>());
            }
            if (CollectionUtils.isNotEmpty(source.getAssociatedClientIds())) {
                target.getAssociatedClients().addAll(associateClients(source.getAssociatedClientIds()));
            }
        }
        target.setShouldRemovePrimaryContacts(source.getShouldRemovePrimaryContacts());
        //todo - if role changes from PRS - delete all associations?
        return target;
    }

    private List<Client> associateClients(List<Long> associatedClientIds) {
        return associatedClientIds.stream()
                .map(id -> clientDao.getOne(id))
                .collect(Collectors.toList());
    }
}
