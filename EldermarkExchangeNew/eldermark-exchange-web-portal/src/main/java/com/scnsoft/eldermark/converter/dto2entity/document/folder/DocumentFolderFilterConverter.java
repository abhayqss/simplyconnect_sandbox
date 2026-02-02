package com.scnsoft.eldermark.converter.dto2entity.document.folder;

import com.scnsoft.eldermark.beans.DocumentFolderFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderFilterDto;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class DocumentFolderFilterConverter implements Converter<DocumentFolderFilterDto, DocumentFolderFilter> {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public DocumentFolderFilter convert(DocumentFolderFilterDto source) {

        var target = new DocumentFolderFilter();

        if (source.isCanUpload()) {
            target.setPermissionLevels(EnumSet.of(
                    DocumentFolderPermissionLevelCode.ADMIN,
                    DocumentFolderPermissionLevelCode.UPLOADER
            ));
        } else {
            target.setPermissionLevels(EnumSet.allOf(DocumentFolderPermissionLevelCode.class));
        }

        if (CollectionUtils.isNotEmpty(source.getTypes())) {
            target.setTypes(source.getTypes());
        } else {
            target.setTypes(List.of(DocumentFolderType.values()));
        }

        target.setPermissionFilter(permissionFilterService.createPermissionFilterForCurrentUser());

        if (source.getOrganizationId() != null) {
            target.setCommunityIds(
                    communityService.findAllByOrgId(source.getOrganizationId()).stream()
                            .map(IdAware::getId)
                            .collect(Collectors.toList())
            );
        } else {
            target.setCommunityIds(source.getCommunityIds());
        }
        return target;
    }
}
