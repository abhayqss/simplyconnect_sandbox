package com.scnsoft.eldermark.converter.entity2dto.document.folder;

import com.scnsoft.eldermark.dto.document.folder.DocumentFolderPermissionLevelDto;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentFolderPermissionLevelDtoConverter implements Converter<DocumentFolderPermissionLevel, DocumentFolderPermissionLevelDto> {

    @Override
    public DocumentFolderPermissionLevelDto convert(DocumentFolderPermissionLevel source) {
        var result = new DocumentFolderPermissionLevelDto();
        result.setId(source.getId());
        result.setTitle(source.getTitle());
        return result;
    }
}
