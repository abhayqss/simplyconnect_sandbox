package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode.ADMIN;
import static com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode.UPLOADER;
import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("documentRegularFolderSecurityService")
@Transactional(readOnly = true)
public class DocumentRegularFolderSecurityService extends BaseDocumentFolderSecurityService {

    @Override
    public boolean canAdd(DocumentFolderSecurityFieldsAware folder) {

        var folderPermissions = folderService.resolveFolderPermissions(folder.getParentId());

        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    folder.getCommunityId(),
                    COMMUNITY_FOLDER_ADD_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FOLDER_ADD_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FOLDER_ADD_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FOLDER_ADD_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(folder.getCommunityId()) &&
                    hasAnyFolderPermission(folderPermissions, List.of(ADMIN, UPLOADER));
        }
    }

    @Override
    public boolean canEdit(Long id) {
        var folder = folderService.findById(id);
        if (folder.getTemporaryDeletionTime() != null) {
            return false;
        }
        return hasPermissions(
                id,
                COMMUNITY_FOLDER_EDIT_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_FOLDER_EDIT_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_FOLDER_EDIT_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_FOLDER_EDIT_IF_CO_RP_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canDelete(Long id) {
        return hasPermissions(
                id,
                COMMUNITY_FOLDER_DELETE_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_FOLDER_DELETE_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_FOLDER_DELETE_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_FOLDER_DELETE_IF_CO_RP_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canRestore(Long id) {
        return canDelete(id);
    }

    private boolean hasPermissions(
            Long id,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission ifCoRegularCommunityCtm,
            Permission ifCoRpCommunityCtm
    ) {
        var folder = folderService.findById(id);

        var folderPermissions = folderService.resolveFolderPermissions(folder);

        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    folder.getCommunityId(),
                    ifAssociatedOrganizationPermission,
                    ifAssociatedCommunityPermission,
                    ifCoRegularCommunityCtm,
                    ifCoRpCommunityCtm
            );
        } else {
            return isEligibleForDiscoveryCommunity(folder.getCommunityId()) &&
                    hasFolderPermission(folderPermissions, ADMIN);
        }
    }
}
