package com.scnsoft.eldermark.converter.entity2dto.document.folder;

import com.scnsoft.eldermark.dto.document.folder.DocumentFolderItemDto;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentFolderItemDtoConverter implements Converter<DocumentFolder, DocumentFolderItemDto> {

    @Autowired
    private DocumentFolderService documentFolderService;

    @Override
    public DocumentFolderItemDto convert(DocumentFolder source) {
        var dto = new DocumentFolderItemDto();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setCommunityId(source.getCommunityId());
        if (source.getParentId() != null) {
            dto.setParentName(documentFolderService.findById(source.getParentId()).getName());
        }
        return dto;
    }
}
