package com.scnsoft.eldermark.converter.entity2dto.document.folder;

import com.scnsoft.eldermark.dto.document.folder.DocumentFolderPermissionDto;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentFolderPermissionDtoConverter implements Converter<DocumentFolderPermission, DocumentFolderPermissionDto> {

    @Override
    public DocumentFolderPermissionDto convert(DocumentFolderPermission source) {
        var dto = new DocumentFolderPermissionDto();
        dto.setId(source.getId());
        dto.setContactId(source.getEmployeeId());
        dto.setContactLogin(source.getEmployee().getLoginName());
        dto.setContactFullName(source.getEmployee().getFullName());
        if (source.getEmployee().getCareTeamRole() != null) {
            dto.setContactSystemRoleId(source.getEmployee().getCareTeamRole().getId());
            dto.setContactSystemRoleTitle(source.getEmployee().getCareTeamRole().getName());
        }
        dto.setPermissionLevelTitle(source.getPermissionLevel().getTitle());
        dto.setPermissionLevelId(source.getPermissionLevel().getId());
        return dto;
    }
}
