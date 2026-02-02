package com.scnsoft.eldermark.converter.entity2dto.document;

import com.scnsoft.eldermark.dto.document.DocumentAndFolderItemDto;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder;
import com.scnsoft.eldermark.entity.document.DocumentAndFolderType;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.service.document.CommunityDocumentSecurityService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.document.DocumentAndFolderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CommunityDocumentAndFolderItemDtoConverter implements Converter<CommunityDocumentAndFolder, DocumentAndFolderItemDto> {

    @Autowired
    private Converter<DocumentCategory, DocumentCategoryItemDto> categoryConverter;

    @Autowired
    @Qualifier("documentFolderSecurityService")
    private DocumentFolderSecurityService folderSecurityService;

    @Autowired
    private CommunityDocumentSecurityService documentSecurityService;

    @Autowired
    private DocumentSignatureTemplateSecurityService templateSecurityService;

    @Override
    public DocumentAndFolderItemDto convert(CommunityDocumentAndFolder source) {
        var dto = new DocumentAndFolderItemDto();

        dto.setId(source.getId());
        if (DocumentAndFolderUtils.isFolderId(source.getId())) {
            dto.setFolderId(DocumentAndFolderUtils.getFolderId(source.getId()));
        }
        dto.setTitle(source.getTitle());
        dto.setDescription(source.getDescription());
        if (source.getAuthor() != null) {
            dto.setAuthor(source.getAuthor().getFullName());
        } else {
            dto.setAuthor("System");
        }
        dto.setLastModifiedDate(DateTimeUtils.toEpochMilli(source.getLastModifiedTime()));
        dto.setMimeType(source.getMimeType());
        dto.setSize(source.getSize());
        dto.setType(source.getType());
        dto.setIsTemporarilyDeleted(source.getTemporaryDeletionTime() != null);
        dto.setIsSecurityEnabled(source.getSecurityEnabled() != null && source.getSecurityEnabled());

        if (source.getCategories() != null) {
            dto.setCategories(
                source.getCategories().stream()
                    .map(categoryConverter::convert)
                    .collect(Collectors.toList())
            );
        }

        if (source.getType().isFolderType()) {
            var folderId = DocumentAndFolderUtils.getFolderId(source.getId());
            dto.setCanEdit(folderSecurityService.canEdit(folderId));
            dto.setCanDelete(folderSecurityService.canDelete(folderId));
            dto.setCanView(folderSecurityService.canView(folderId));
        } else if (source.getType() == DocumentAndFolderType.CUSTOM) {
            var documentId = DocumentAndFolderUtils.getDocumentId(source.getId());
            dto.setCanEdit(documentSecurityService.canEdit(documentId));
            dto.setCanDelete(documentSecurityService.canDelete(documentId));
            dto.setCanView(true);
        } else if (source.getType() == DocumentAndFolderType.TEMPLATE) {
            var templateId = DocumentAndFolderUtils.getTemplateId(source.getId());
            dto.setTemplateId(templateId);
            dto.setCanEdit(templateSecurityService.canEdit(templateId));
            dto.setCanDelete(templateSecurityService.canDelete(templateId));
            dto.setCanView(true);
            dto.setCanAssign(templateSecurityService.canAssign(templateId));
            dto.setCanCopy(templateSecurityService.canCopy(templateId));
            dto.setStatusName(source.getTemplateStatus());
            dto.setStatusTitle(source.getTemplateStatus().getDisplayName());
        }
        return dto;
    }
}
