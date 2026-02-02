package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.UserDto;
import com.scnsoft.eldermark.dto.employee.EmployeeAssociatedClientDto;
import com.scnsoft.eldermark.entity.Avatar;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.audit.AuditLogService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EmployeeToUserDtoConverter implements Converter<Employee, UserDto> {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private Converter<Client, EmployeeAssociatedClientDto> employeeAssociatedClientDtoConverter;

    @Override
    public UserDto convert(Employee source) {
        UserDto userDto = new UserDto();
        userDto.setStatus(source.getStatus().getText());
        userDto.setCommunityId(source.getCommunityId());
        if (source.getCommunityId() != null) {
            userDto.setCommunityName(source.getCommunity().getName());
        }
        userDto.setId(source.getId());
        userDto.setFirstName(source.getFirstName());
        userDto.setLastName(source.getLastName());
        userDto.setFullName(source.getFullName());
        userDto.setOrganizationId(source.getOrganizationId());
        userDto.setOrganizationName(source.getOrganization() != null ? source.getOrganization().getName() : null);
        userDto.setRoleTitle(source.getCareTeamRole().getDisplayName());
        userDto.setRoleName(source.getCareTeamRole().getCode().getCode());
        userDto.setLastLoginDate(DateTimeUtils.toEpochMilli(auditLogService.findLastLoginTime(source.getId())));
        userDto.setEmail(PersonTelecomUtils.findValue(source.getPerson(), PersonTelecomCode.EMAIL).orElse(null));
        userDto.setAvatarId(Optional.ofNullable(source.getAvatar()).map(Avatar::getId).orElse(null));

        if (CollectionUtils.isNotEmpty(source.getAssociatedClients())) {
            var associatedClients = source.getAssociatedClients().stream()
                    .map(employeeAssociatedClientDtoConverter::convert)
                    .collect(Collectors.toList());

            userDto.setAssociatedClients(associatedClients);
        }

        return userDto;
    }


}
