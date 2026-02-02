package com.scnsoft.eldermark.mobile.converters;

import com.scnsoft.eldermark.entity.Avatar;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.mobile.dto.UserDto;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EmployeeToUserDtoConverter implements Converter<Employee, UserDto> {

    @Autowired
    private ClientService clientService;

    @Override
    public UserDto convert(Employee source) {
        UserDto userDto = new UserDto();
        userDto.setCommunityId(source.getCommunityId());
        if (source.getCommunityId() != null) {
            userDto.setCommunityName(source.getCommunity().getName());
        }
        userDto.setId(source.getId());
        userDto.setFirstName(source.getFirstName());
        userDto.setLastName(source.getLastName());
        userDto.setOrganizationId(source.getOrganizationId());
        userDto.setOrganizationName(source.getOrganization() != null ? source.getOrganization().getName() : null);
        userDto.setRoleTitle(source.getCareTeamRole().getDisplayName());
        userDto.setRoleName(source.getCareTeamRole().getCode().getCode());
        userDto.setEmail(PersonTelecomUtils.findValue(source.getPerson(), PersonTelecomCode.EMAIL).orElse(null));

        if (source.getAvatar() != null) {
            userDto.setAvatarId(source.getAvatar().getId());
            userDto.setAvatarName(source.getAvatar().getAvatarName());
        }

        userDto.setStatus(source.getStatus().name());

        if (CollectionUtils.isNotEmpty(source.getAssociatedClients())) {
            var hasAnyUnconfirmedPolicy = source.getAssociatedClients().stream()
                    .anyMatch(client -> !clientService.hasConfirmedHieConsentPolicy(client));
            userDto.setShouldConfirmHieConsentPolicy(hasAnyUnconfirmedPolicy);
        }

        return userDto;
    }
}
