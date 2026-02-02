package com.scnsoft.eldermark.converter.entity2dto.prospect;

import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.prospect.ProspectPrimaryContact;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ProspectPrimaryContactDtoConverter implements Converter<ProspectPrimaryContact, PrimaryContactDto> {

    @Override
    public PrimaryContactDto convert(ProspectPrimaryContact source) {
        PrimaryContactDto primaryContactDto = null;
        if (source != null) {
            primaryContactDto = new PrimaryContactDto();
            primaryContactDto.setNotificationMethodName(source.getNotificationMethod().name());
            primaryContactDto.setNotificationMethodTitle(source.getNotificationMethod().getDisplayName());
            primaryContactDto.setTypeName(source.getType().name());
            primaryContactDto.setTypeTitle(source.getType().getDisplayName());
            if (source.getProspectCareTeamMember() != null) {
                var careTeamMember = source.getProspectCareTeamMember();
                primaryContactDto.setCareTeamMemberId(careTeamMember.getId());
                primaryContactDto.setFirstName(careTeamMember.getEmployee().getFirstName());
                primaryContactDto.setLastName(careTeamMember.getEmployee().getLastName());
                primaryContactDto.setActive(careTeamMember.getEmployee().getStatus() == EmployeeStatus.ACTIVE);
            }
        }
        return primaryContactDto;
    }
}
