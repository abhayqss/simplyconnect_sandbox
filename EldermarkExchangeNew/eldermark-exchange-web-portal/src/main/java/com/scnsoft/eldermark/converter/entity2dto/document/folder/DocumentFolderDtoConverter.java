package com.scnsoft.eldermark.converter.entity2dto.document.folder;

import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderPermissionDto;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.document.folder.FolderPermission;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode.ADMIN;

@Component
public class DocumentFolderDtoConverter implements Converter<DocumentFolder, DocumentFolderDto> {

    @Autowired
    private Converter<DocumentCategory, DocumentCategoryItemDto> categoryConverter;

    @Autowired
    private DocumentCategoryService categoryService;

    @Autowired
    private Converter<DocumentFolderPermission, DocumentFolderPermissionDto> permissionConverter;

    @Autowired
    @Qualifier("documentFolderSecurityService")
    private DocumentFolderSecurityService folderSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private DocumentFolderService folderService;

    @Override
    public DocumentFolderDto convert(DocumentFolder source) {
        var dto = new DocumentFolderDto();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setParentId(source.getParentId());
        dto.setCommunityId(source.getCommunityId());
        dto.setType(source.getType());
        dto.setIsSecurityEnabled(source.getIsSecurityEnabled());

        if (source.getId() != null) {
            dto.setCanDelete(folderSecurityService.canDelete(source.getId()));
            dto.setCanEdit(folderSecurityService.canEdit(source.getId()));
        }

        if (dto.getType() != DocumentFolderType.TEMPLATE) {
            fillPermissions(source, dto);
        }

        if (source.getCategoryChainIds() != null) {
            dto.setCategories(categoryService.findAllByCategoryChainIds(source.getCategoryChainIds()).stream()
                .map(categoryConverter::convert)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    private void fillPermissions(DocumentFolder source, DocumentFolderDto dto) {
        if (source.getPermissions() != null) {
            dto.setPermissions(
                    source.getPermissions().stream()
                            .sorted(
                                    Comparator.<DocumentFolderPermission, Integer>comparing(it -> it.getPermissionLevel().getCode().getPriority())
                                            .thenComparing(it -> it.getEmployee().getFullName())
                            )
                            .map(permissionConverter::convert)
                            .collect(Collectors.toList())
            );

            if (dto.getId() == null || Boolean.TRUE.equals(dto.getCanEdit())) {
                var unmodifiableEmployeeIds = new HashSet<Long>();
                unmodifiableEmployeeIds.add(loggedUserService.getCurrentEmployeeId());

                var parentPermissions = folderService.resolveFolderPermissions(source.getParentId());
                if (CollectionUtils.isNotEmpty(parentPermissions)) {
                    parentPermissions.stream()
                            .filter(it -> it.getPermissionLevel().getCode() == ADMIN)
                            .map(FolderPermission::getEmployeeId)
                            .forEach(unmodifiableEmployeeIds::add);
                }

                dto.getPermissions()
                        .forEach(it -> {
                            var editable = !unmodifiableEmployeeIds.contains(it.getContactId());
                            it.setCanEdit(editable);
                            it.setCanDelete(editable);
                        });
            } else {
                dto.getPermissions()
                        .forEach(it -> {
                            it.setCanEdit(false);
                            it.setCanDelete(false);
                        });
            }
        }
    }
}
