package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.CommunityDocumentFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder;
import com.scnsoft.eldermark.entity.document.DocumentAndFolderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommunityDocumentAndFolderService {

    Page<CommunityDocumentAndFolder> find(CommunityDocumentFilter documentFilter, Pageable pageable);

    long count(CommunityDocumentFilter documentFilter);

    <T> List<T> findByIds(List<String> ids, Class<T> projectionType);

    long countByParentId(Long parentFolderId);

    DocumentTreeItem<CommunityDocumentAndFolder> getDocumentTree(
            Long communityId,
            Long rootFolderId,
            PermissionFilter permissionFilter
    );

    DocumentTreeItem<CommunityDocumentAndFolder> getDocumentTree(
            Long communityId,
            Long rootFolderId,
            PermissionFilter permissionFilter,
            List<String> filterIds
    );

    Optional<Instant> getOldestDate(Long communityId);

    InputStream readDocument(CommunityDocumentAndFolder document);

    <P> P fetchByIdAndType(String id, DocumentAndFolderType type, Class<P> projectionClass);
}
