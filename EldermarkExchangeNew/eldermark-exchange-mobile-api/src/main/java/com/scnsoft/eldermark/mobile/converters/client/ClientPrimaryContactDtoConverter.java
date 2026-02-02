package com.scnsoft.eldermark.mobile.converters.client;

import com.scnsoft.eldermark.entity.client.ClientPrimaryContact;
import com.scnsoft.eldermark.mobile.dto.client.ClientPrimaryContactDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientPrimaryContactDtoConverter implements Converter<ClientPrimaryContact, ClientPrimaryContactDto> {

    @Override
    public ClientPrimaryContactDto convert(ClientPrimaryContact source) {

        var dto = new ClientPrimaryContactDto();

        if (source.getClientCareTeamMemberId() != null) {
            var ctm = source.getClientCareTeamMember();
            dto.setCareTeamMemberId(ctm.getId());
            dto.setFirstName(ctm.getEmployee().getFirstName());
            dto.setLastName(ctm.getEmployee().getLastName());
            dto.setRoleName(ctm.getEmployee().getCareTeamRole().getCode().getCode());
            dto.setRoleTitle(ctm.getEmployee().getCareTeamRole().getDisplayName());
        }

        dto.setType(source.getType());

        return dto;
    }
}
