package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;

import java.util.List;

public interface DocumentFolderSecurityService {

    boolean canViewList(DocumentFolderSecurityFieldsAware folder);

    boolean canViewListInOrganization(Long organizationId);

    boolean canViewListInCommunities(List<Long> communityIds);

    boolean canAdd(DocumentFolderSecurityFieldsAware folder);

    boolean canView(Long id);

    boolean canEdit(Long id);

    boolean canDelete(Long id);

    boolean canDownload(Long id);

    boolean canRestore(Long id);

    boolean hasAnyFolderPermission(List<FolderPermission> permissions, List<DocumentFolderPermissionLevelCode> levels);

    boolean hasFolderPermission(List<FolderPermission> permissions, DocumentFolderPermissionLevelCode level);
}
