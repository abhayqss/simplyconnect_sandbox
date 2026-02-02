package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.client.EmergencyContactListItemDto;
import com.scnsoft.eldermark.entity.Avatar;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmergencyContactListDtoConverter implements ListAndItemConverter<ClientCareTeamMember, EmergencyContactListItemDto> {

    @Override
    public EmergencyContactListItemDto convert(ClientCareTeamMember source) {
        if (source != null) {
            EmergencyContactListItemDto target = new EmergencyContactListItemDto();
            target.setId(source.getEmployee().getId());
            target.setFirstName(source.getEmployee().getFirstName());
            target.setLastName(source.getEmployee().getLastName());
            target.setFullName(source.getEmployee().getFullName());
            target.setRelationship(source.getCareTeamRelationship() != null ? source.getCareTeamRelationship().getName() : null);
            target.setEmail(getEmail(source.getEmployee().getPerson()));
            target.setPhone(getPhoneNumber(source.getEmployee().getPerson()));
            target.setAvatarId(getAvatarId(source.getEmployee().getAvatar()));
            return target;
        }
        return null;
    }

    private String getEmail(Person person) {
        return PersonTelecomUtils.findValue(person, PersonTelecomCode.EMAIL, null);
    }

    private String getPhoneNumber(Person person) {
        return PersonTelecomUtils.findValue(person, PersonTelecomCode.MC)
                .orElse(PersonTelecomUtils.findValue(person, PersonTelecomCode.WP)
                        .orElse(PersonTelecomUtils.findValue(person, PersonTelecomCode.HP).orElse(null)));
    }

    private Long getAvatarId(Avatar avatar) {
        return Optional.ofNullable(avatar).map(Avatar::getId).orElse(null);
    }
}