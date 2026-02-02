package com.scnsoft.eldermark.converter.entity2dto.document.folder;

import com.scnsoft.eldermark.dto.document.folder.PermissionContactDto;
import com.scnsoft.eldermark.entity.Employee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PermissionContactDtoConverter implements Converter<Employee, PermissionContactDto> {

    @Override
    public PermissionContactDto convert(Employee source) {
        var dto = new PermissionContactDto();
        dto.setContactId(source.getId());
        dto.setContactLogin(source.getLoginName());
        dto.setContactFullName(source.getFullName());
        if (source.getCareTeamRole() != null) {
            dto.setContactSystemRoleId(source.getCareTeamRole().getId());
            dto.setContactSystemRoleTitle(source.getCareTeamRole().getName());
        }
        return dto;
    }
}
