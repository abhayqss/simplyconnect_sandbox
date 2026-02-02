package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.document.community.CommunityDocumentService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.security.BaseSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode.*;
import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("communityDocumentSecurityService")
public class CommunityDocumentSecurityServiceImpl extends BaseSecurityService implements CommunityDocumentSecurityService {

    @Autowired
    @Qualifier("documentFolderSecurityService")
    private DocumentFolderSecurityService folderSecurityService;

    @Autowired
    private DocumentFolderService folderService;

    @Autowired
    private CommunityDocumentService communityDocumentService;

    @Override
    //todo refactor with DocumentFolderSecurityServiceImpl
    public boolean canViewList(CommunityDocumentSecurityFieldsAware dto) {
        var folder = dto.getFolderId() != null ? folderService.findById(dto.getFolderId()) : null;
        if (folder != null) {
            if (!Objects.equals(folder.getCommunityId(), dto.getCommunityId())) {
                return false;
            }
            if (folder.getTemporaryDeletionTime() != null) {
                return false;
            }
        }
        var folderPermissions = folderService.resolveFolderPermissions(folder);
        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    dto.getCommunityId(),
                    COMMUNITY_FILE_VIEW_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FILE_VIEW_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FILE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FILE_VIEW_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(dto.getCommunityId()) &&
                    folderSecurityService.hasAnyFolderPermission(folderPermissions, List.of(ADMIN, UPLOADER, VIEWER));
        }
    }

    @Override
    public boolean canUpload(CommunityDocumentSecurityFieldsAware dto) {
        var folder = dto.getFolderId() != null ? folderService.findById(dto.getFolderId()) : null;
        if (folder != null) {
            if (!Objects.equals(folder.getCommunityId(), dto.getCommunityId())) {
                return false;
            }
            if (folder.getTemporaryDeletionTime() != null) {
                return false;
            }
            if (folder.getType() == DocumentFolderType.TEMPLATE) {
                return false;
            }
        }

        var folderPermissions = folderService.resolveFolderPermissions(folder);
        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    dto.getCommunityId(),
                    COMMUNITY_FILE_ADD_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FILE_ADD_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FILE_ADD_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FILE_ADD_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(dto.getCommunityId()) &&
                    folderSecurityService.hasAnyFolderPermission(folderPermissions, List.of(ADMIN, UPLOADER));
        }
    }

    @Override
    public boolean canView(long documentId) {
        return canDownload(documentId);
    }

    @Override
    public boolean canDownload(long documentId) {
        return canDownload(communityDocumentService.findSecurityAwareEntity(documentId));
    }

    @Override
    public boolean canDownload(CommunityDocumentSecurityFieldsAware dto) {
        var folder = dto.getFolderId() != null ? folderService.findById(dto.getFolderId()) : null;
        if (folder != null) {
            if (!Objects.equals(folder.getCommunityId(), dto.getCommunityId())) {
                return false;
            }
            if (folder.getTemporaryDeletionTime() != null) {
                return false;
            }
        }
        var folderPermissions = folderService.resolveFolderPermissions(folder);

        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    dto.getCommunityId(),
                    COMMUNITY_FILE_VIEW_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FILE_VIEW_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FILE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FILE_VIEW_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(dto.getCommunityId()) && folderSecurityService.hasAnyFolderPermission(folderPermissions, List.of(ADMIN, UPLOADER, VIEWER));
        }
    }

    @Override
    public boolean canDelete(long documentId) {
        return canDelete(communityDocumentService.findSecurityAwareEntity(documentId));
    }

    @Override
    public boolean canDelete(CommunityDocumentSecurityFieldsAware dto) {
        var folder = dto.getFolderId() != null ? folderService.findById(dto.getFolderId()) : null;
        if (folder != null) {
            if (!Objects.equals(folder.getCommunityId(), dto.getCommunityId())) {
                return false;
            }
            if (folder.getTemporaryDeletionTime() != null) {
                return false;
            }
        }
        var folderPermissions = folderService.resolveFolderPermissions(folder);
        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    dto.getCommunityId(),
                    COMMUNITY_FILE_DELETE_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FILE_DELETE_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FILE_DELETE_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FILE_DELETE_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(dto.getCommunityId()) &&
                    folderSecurityService.hasAnyFolderPermission(folderPermissions, List.of(ADMIN));
        }
    }

    @Override
    public boolean canEdit(long documentId) {
        return canEdit(communityDocumentService.findSecurityAwareEntity(documentId));
    }

    @Override
    public boolean canEdit(CommunityDocumentSecurityFieldsAware dto) {
        var folder = dto.getFolderId() != null ? folderService.findById(dto.getFolderId()) : null;
        if (folder != null) {
            if (!Objects.equals(folder.getCommunityId(), dto.getCommunityId())) {
                return false;
            }
            if (folder.getTemporaryDeletionTime() != null) {
                return false;
            }
        }
        var folderPermissions = folderService.resolveFolderPermissions(folder);
        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    dto.getCommunityId(),
                    COMMUNITY_FILE_EDIT_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FILE_EDIT_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FILE_EDIT_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FILE_EDIT_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(dto.getCommunityId()) &&
                    folderSecurityService.hasAnyFolderPermission(folderPermissions, List.of(ADMIN, UPLOADER));
        }
    }

    private boolean hasDocumentPermissions(
            Long communityId,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission ifCoRegularCommunityCtm,
            Permission ifCoPrCommunityCtm
    ) {
        var community = communityService.findSecurityAwareEntity(communityId);
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (permissionFilter.hasPermission(ifAssociatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganizationPermission);

            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifAssociatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedCommunityPermission);
            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifCoRegularCommunityCtm)) {
            var employees = permissionFilter.getEmployees(ifCoRegularCommunityCtm);
            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifCoPrCommunityCtm)) {
            var employees = permissionFilter.getEmployees(ifCoPrCommunityCtm);
            return isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold()
            );
        }

        return false;
    }
}
