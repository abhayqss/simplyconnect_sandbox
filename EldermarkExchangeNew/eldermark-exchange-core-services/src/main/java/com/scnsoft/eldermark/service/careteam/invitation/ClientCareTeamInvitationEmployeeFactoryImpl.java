package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.careteam.invitation.InviteCareTeamMemberData;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.PersonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
class ClientCareTeamInvitationEmployeeFactoryImpl implements ClientCareTeamInvitationEmployeeFactory {

    @Autowired
    private ContactService contactService;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Override
    public Employee createNewPendingContact(InviteCareTeamMemberData invitationData) {
        var employee = new Employee();
        var org = organizationDao.findByAlternativeId(CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID);
        employee.setOrganization(org);
        employee.setOrganizationId(org.getId());
        employee.setCompany(org.getSystemSetup().getLoginCompanyId());
        CareCoordinationConstants.setLegacyId(employee);

        //todo maybe add something to uniquely identify community in case we'll need multiple communities in family app
        var community = communityDao.findFirstByOrganizationId(org.getId());
        employee.setCommunity(community);
        employee.setCommunityId(community.getId());

        employee.setPerson(createPerson(org, invitationData));

        var role = careTeamRoleService.get(CareTeamRoleCode.ROLE_PARENT_GUARDIAN);
        employee.setCareTeamRole(role);
        employee.setCareTeamRoleId(role.getId());

        employee.setStatus(EmployeeStatus.PENDING);
        employee.setPassword("password");

        employee.setFirstName(invitationData.getFirstName());
        employee.setLastName(invitationData.getLastName());
        employee.setLoginName(invitationData.getEmail());
        employee.setBirthDate(invitationData.getBirthDate());

        employee.setCreatedAutomatically(false);

        return contactService.save(employee, Optional.ofNullable(invitationData.getCreatedByEmployee()).map(BasicEntity::getId).orElse(null));
    }

    private Person createPerson(Organization organization, InviteCareTeamMemberData invitation) {
        var person = CareCoordinationUtils.createNewPerson(organization);
        CareCoordinationUtils.createAndAddName(person, invitation.getFirstName(), invitation.getLastName());
        createAndAddEmail(person, invitation.getEmail());
        return person;
    }

    private void createAndAddEmail(Person person, String email) {
        PersonUtils.createPersonTelecom(
                person,
                PersonTelecomCode.EMAIL,
                email,
                CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE
        );
    }
}
