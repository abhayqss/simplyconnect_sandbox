package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("documentTemplateFolderSecurityService")
@Transactional(readOnly = true)
public class DocumentTemplateFolderSecurityService extends BaseDocumentFolderSecurityService {

    @Override
    public boolean canAdd(DocumentFolderSecurityFieldsAware folder) {

        if (folder.getParentId() == null) {
            return false;
        }

        var parent = folderService.findById(folder.getParentId());

        if (parent.getParentId() != null) {
            return false;
        }

        return hasDocumentPermissions(
                folder.getCommunityId(),
                COMMUNITY_TEMPLATE_FOLDER_ADD_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_TEMPLATE_FOLDER_ADD_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_TEMPLATE_FOLDER_ADD_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_TEMPLATE_FOLDER_ADD_IF_CO_RP_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canEdit(Long id) {
        var folder = folderService.findById(id);
        if (folder.getTemporaryDeletionTime() != null) {
            return false;
        }
        return hasPermissions(
                id,
                COMMUNITY_TEMPLATE_FOLDER_EDIT_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_TEMPLATE_FOLDER_EDIT_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_TEMPLATE_FOLDER_EDIT_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_TEMPLATE_FOLDER_EDIT_IF_CO_RP_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canDelete(Long id) {
        return hasPermissions(
                id,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_CO_RP_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canRestore(Long id) {
        return hasPermissions(
                id,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_ASSOCIATED_COMMUNITY,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_CO_REGULAR_COMMUNITY_CTM,
                COMMUNITY_TEMPLATE_FOLDER_DELETE_IF_CO_RP_COMMUNITY_CTM
        );
    }

    private boolean hasPermissions(
            Long id,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission ifCoRegularCommunityCtm,
            Permission ifCoRpCommunityCtm
    ) {
        var folder = folderService.findById(id);

        if (folder.getParentId() == null) {
            return false;
        }

        return hasDocumentPermissions(
                folder.getCommunityId(),
                ifAssociatedOrganizationPermission,
                ifAssociatedCommunityPermission,
                ifCoRegularCommunityCtm,
                ifCoRpCommunityCtm
        );
    }
}
