package com.scnsoft.eldermark.mobile.converters.client;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContact;
import com.scnsoft.eldermark.mobile.dto.client.ClientDto;
import com.scnsoft.eldermark.mobile.dto.client.ClientPrimaryContactDto;
import com.scnsoft.eldermark.mobile.projection.client.MobileClientListInfo;
import com.scnsoft.eldermark.service.security.*;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientDtoConverter
        extends BaseClientDtoConverter
        implements Converter<Client, ClientDto> {

    @Autowired
    private Converter<ClientPrimaryContact, ClientPrimaryContactDto> clientPrimaryContactDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientCareTeamSecurityService clientCareTeamSecurityService;

    @Autowired
    private ClientMedicationSecurityService medicationSecurityService;

    @Autowired
    private ClientLocationHistorySecurityService clientLocationHistorySecurityService;

    @Autowired
    private ClientCareTeamInvitationSecurityService clientCareTeamInvitationSecurityService;

    @Override
    public ClientDto convert(Client source) {
        var dto = new ClientDto();
        fillListItem(new EntityBackedMobileClientListInfo(source, loggedUserService.getCurrentEmployeeId()),
                dto,
                permissionFilterService.createPermissionFilterForCurrentUser()
        );

        dto.setUnit(source.getUnitNumber());
        if (source.getGender() != null) {
            dto.setGenderId(source.getGender().getId());
            dto.setGender(source.getGender().getDisplayName());
        }

        dto.setBirthDate(DateTimeUtils.formatLocalDate(source.getBirthDate()));
        dto.setCellPhone(PersonTelecomUtils.findValue(source.getPerson(), PersonTelecomCode.MC)
                .orElse(null));

        if (source.getPrimaryContactId() != null) {
            dto.setPrimaryContact(clientPrimaryContactDtoConverter.convert(source.getPrimaryContact()));
        }

        dto.setCanViewCareTeam(clientCareTeamSecurityService.canViewList(source.getId()));
        dto.setCanViewMedications(medicationSecurityService.canViewOfClient(source.getId()));

        dto.setCanViewLocationHistory(clientLocationHistorySecurityService.canViewList(source.getId()));
        dto.setCanReportLocation(clientLocationHistorySecurityService.canAdd(source::getId));

        dto.setCanInviteCareTeamMember(clientCareTeamInvitationSecurityService.canInvite(source.getId()));

        dto.setHieConsentPolicyName(source.getHieConsentPolicyType());

        return dto;
    }

    private static class EntityBackedMobileClientListInfo implements MobileClientListInfo {

        private final Client client;
        private final Long loggedEmployeeId;

        private EntityBackedMobileClientListInfo(Client client, Long loggedEmployeeId) {
            this.client = client;
            this.loggedEmployeeId = loggedEmployeeId;
        }

        @Override
        public Long getAvatarId() {
            return client.getAvatar() != null ? client.getAvatar().getId() : null;
        }

        @Override
        public String getAvatarAvatarName() {
            return client.getAvatar() != null
                    ? client.getAvatar().getAvatarName()
                    : null;
        }

        @Override
        public String getFirstName() {
            return client.getFirstName();
        }

        @Override
        public String getLastName() {
            return client.getLastName();
        }

        @Override
        public String getMiddleName() {
            return client.getMiddleName();
        }

        @Override
        public Long getAssociatedEmployeeId() {
            return client.getAssociatedEmployee() != null ?
                    client.getAssociatedEmployee().getId()
                    : null;
        }

        @Override
        public String getAssociatedEmployeeTwilioUserSid() {
            return client.getAssociatedEmployee() != null ?
                    client.getAssociatedEmployee().getTwilioUserSid()
                    : null;
        }

        @Override
        public Boolean getIsFavourite() {
            return client.getAddedAsFavouriteToEmployeeIds().contains(loggedEmployeeId);
        }

        @Override
        public String getCommunityName() {
            return client.getCommunity() != null ?
                    client.getCommunity().getName()
                    : null;
        }

        @Override
        public Long getId() {
            return client.getId();
        }

        @Override
        public Long getCommunityId() {
            return client.getCommunityId();
        }

        @Override
        public Long getOrganizationId() {
            return client.getOrganizationId();
        }

        @Override
        public String getOrganizationName() {
            return client.getOrganization() != null ? client.getOrganization().getName() : null;
        }
    }
}
