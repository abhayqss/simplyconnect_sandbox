package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateSecurityService;
import com.scnsoft.eldermark.service.security.BaseSecurityService;
import com.scnsoft.eldermark.util.document.DocumentAndFolderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("communityDocumentAndFolderSecurityService")
public class CommunityDocumentAndFolderSecurityServiceImpl extends BaseSecurityService implements CommunityDocumentAndFolderSecurityService {

    private static final List<Permission> VIEW_LIST_PERMISSIONS = List.of(
            COMMUNITY_FOLDER_VIEW_IF_ASSOCIATED_ORGANIZATION,
            COMMUNITY_FOLDER_VIEW_IF_ASSOCIATED_COMMUNITY,
            COMMUNITY_FOLDER_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
            COMMUNITY_FOLDER_VIEW_IF_CO_RP_COMMUNITY_CTM,
            COMMUNITY_FILE_VIEW_IF_ASSOCIATED_ORGANIZATION,
            COMMUNITY_FILE_VIEW_IF_ASSOCIATED_COMMUNITY,
            COMMUNITY_FILE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
            COMMUNITY_FILE_VIEW_IF_CO_RP_COMMUNITY_CTM
    );

    @Autowired
    @Qualifier("documentFolderSecurityService")
    private DocumentFolderSecurityService folderSecurityService;

    @Autowired
    private CommunityDocumentSecurityService documentSecurityService;

    @Autowired
    private DocumentSignatureTemplateSecurityService templateSecurityService;

    @Override
    public boolean canDownloadAll(List<String> ids) {
        return ids.stream()
                .allMatch(id -> {
                    if (DocumentAndFolderUtils.isTemplateFolder(id)) {
                        var templateId = DocumentAndFolderUtils.getTemplateId(id);
                        var communityId = DocumentAndFolderUtils.getCommunityIdFromTemplateFolderId(id);
                        return templateSecurityService.canView(templateId, communityId);
                    } else if (DocumentAndFolderUtils.isFolderId(id)) {
                        return folderSecurityService.canDownload(DocumentAndFolderUtils.getFolderId(id));
                    } else {
                        return documentSecurityService.canDownload(DocumentAndFolderUtils.getDocumentId(id));
                    }
                });
    }

    @Override
    public boolean canViewList() {

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        return permissionFilterService.createPermissionFilterForCurrentUser()
                .hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }
}
