package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ClientPrimaryContactDtoConverter implements Converter<Client, PrimaryContactDto> {

    @Override
    public PrimaryContactDto convert(Client client) {
        var primaryContact = client.getPrimaryContact();
        PrimaryContactDto primaryContactDto = null;
        if (primaryContact != null) {
            primaryContactDto = new PrimaryContactDto();
            primaryContactDto.setNotificationMethodName(primaryContact.getNotificationMethod().name());
            primaryContactDto.setNotificationMethodTitle(primaryContact.getNotificationMethod().getDisplayName());
            primaryContactDto.setTypeName(primaryContact.getType().name());
            primaryContactDto.setTypeTitle(primaryContact.getType().getDisplayName());
            if (primaryContact.getClientCareTeamMember() != null) {
                var careTeamMember = primaryContact.getClientCareTeamMember();
                primaryContactDto.setCareTeamMemberId(careTeamMember.getId());
                primaryContactDto.setFirstName(careTeamMember.getEmployee().getFirstName());
                primaryContactDto.setLastName(careTeamMember.getEmployee().getLastName());
                primaryContactDto.setActive(careTeamMember.getEmployee().getStatus() == EmployeeStatus.ACTIVE);
                primaryContactDto.setRoleName(careTeamMember.getEmployee().getCareTeamRole().getCode().getCode());
                primaryContactDto.setRoleTitle(careTeamMember.getEmployee().getCareTeamRole().getDisplayName());
            }
        }
        return primaryContactDto;
    }
}
