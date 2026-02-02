package com.scnsoft.eldermark.converter.entity2dto.document.community;

import static java.util.Objects.nonNull;

import com.scnsoft.eldermark.dto.document.CommunityDocumentItemDto;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.entity.document.community.CommunityDocument;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.CommunityDocumentSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CommunityDocumentDtoConverter implements Converter<CommunityDocument, CommunityDocumentItemDto> {

    @Autowired
    private Converter<DocumentCategory, DocumentCategoryItemDto> categoryConverter;

    @Autowired
    private CommunityDocumentSecurityService communityDocumentSecurityService;

    @Autowired
    private CommunityService communityService;

    @Override
    public CommunityDocumentItemDto convert(CommunityDocument source) {
        var target = new CommunityDocumentItemDto();

        target.setTemporarilyDeletedDate(DateTimeUtils.toEpochMilli(source.getTemporaryDeletionTime()));
        if (source.getTemporaryDeletedBy() != null) {
            target.setTemporarilyDeletedBy(source.getTemporaryDeletedBy().getFullName());
        }
        target.setAssignedDate(DateTimeUtils.toEpochMilli(source.getCreationTime()));
        if (source.getAuthor() != null) {
            target.setAssignedBy(source.getAuthor().getFullName());
        }

        target.setAuthor(source.getAuthor().getFullName());
        target.setLastModifiedDate(DateTimeUtils.toEpochMilli(source.getLastModifiedTime()));
        target.setId(source.getId());
        target.setTitle(source.getDocumentTitle());
        target.setCreatedDate(DateTimeUtils.toEpochMilli(source.getCreationTime()));
        target.setMimeType(DocumentUtils.resolveMimeType(source));
        target.setSize(source.getSize());

        if (nonNull(source.getCommunityId())){
            Community community = communityService.findById(source.getCommunityId());
            target.setCommunityTitle(community.getName());
            target.setCommunityOid(community.getOid());
        }

        if (source.getCategories() != null) {
            target.setCategories(
                source.getCategories().stream()
                    .map(categoryConverter::convert)
                    .collect(Collectors.toList())
            );
        }

        target.setDescription(source.getDescription());

        target.setCanDelete(communityDocumentSecurityService.canDelete(source));
        target.setCanEdit(communityDocumentSecurityService.canEdit(source));
        target.setIsTemporarilyDeleted(source.getTemporaryDeleted());
        return target;
    }
}
