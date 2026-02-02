package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.security.BaseSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;

import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode.*;
import static com.scnsoft.eldermark.entity.security.Permission.*;

public abstract class BaseDocumentFolderSecurityService extends BaseSecurityService implements DocumentFolderSecurityService {

    @Autowired
    protected DocumentFolderService folderService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public boolean canViewList(DocumentFolderSecurityFieldsAware folder) {
        var folderPermissions = folderService.resolveFolderPermissions(folder.getParentId());

        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    folder.getCommunityId(),
                    COMMUNITY_FOLDER_VIEW_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FOLDER_VIEW_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FOLDER_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FOLDER_VIEW_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(folder.getCommunityId()) &&
                    hasAnyFolderPermission(folderPermissions, List.of(ADMIN, UPLOADER, VIEWER));
        }
    }

    @Override
    public boolean canViewListInOrganization(Long organizationId) {
        return canViewListInCommunities(
                communityService.findAllByOrgId(organizationId).stream()
                        .map(IdAware::getId)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean canViewListInCommunities(List<Long> communityIds) {
        return communityIds.stream()
                .allMatch(communityId -> canViewList(DocumentFolderSecurityFieldsAware.of(communityId)));
    }

    @Override
    public boolean canView(Long id) {
        var folder = folderService.findById(id);
        if (folder.getTemporaryDeletionTime() != null) {
            return false;
        }

        var folderPermissions = folderService.resolveFolderPermissions(folder);

        if (folderPermissions.isEmpty()) {
            return hasDocumentPermissions(
                    folder.getCommunityId(),
                    COMMUNITY_FOLDER_VIEW_IF_ASSOCIATED_ORGANIZATION,
                    COMMUNITY_FOLDER_VIEW_IF_ASSOCIATED_COMMUNITY,
                    COMMUNITY_FOLDER_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
                    COMMUNITY_FOLDER_VIEW_IF_CO_RP_COMMUNITY_CTM
            );
        } else {
            return isEligibleForDiscoveryCommunity(folder.getCommunityId()) &&
                    hasAnyFolderPermission(folderPermissions, List.of(ADMIN, UPLOADER, VIEWER));
        }
    }

    @Override
    public boolean canDownload(Long id) {
        return canView(id);
    }

    @Override
    public boolean hasAnyFolderPermission(List<FolderPermission> permissions, List<DocumentFolderPermissionLevelCode> levels) {

        var currentEmployeeIds = permissionFilterService.createPermissionFilterForCurrentUser().getAllEmployeeIds();

        return permissions.stream()
                .filter(it -> currentEmployeeIds.contains(it.getEmployeeId()))
                .anyMatch(it -> levels.contains(it.getPermissionLevel().getCode()));
    }

    @Override
    public boolean hasFolderPermission(List<FolderPermission> permissions, DocumentFolderPermissionLevelCode level) {
        return hasAnyFolderPermission(permissions, List.of(level));
    }

    protected boolean hasDocumentPermissions(
            Long communityId,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission ifCoRegularCommunityCtm,
            Permission ifCoPrCommunityCtm
    ) {
        if (!isEligibleForDiscoveryCommunity(communityId)) {
            return false;
        }
        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var organizationId = Lazy.of(() -> communityService.findById(communityId, OrganizationIdAware.class).getOrganizationId());

        if (permissionFilter.hasPermission(ifAssociatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganizationPermission);

            if (isAnyCreatedUnderOrganization(employees, organizationId.get())) {
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
