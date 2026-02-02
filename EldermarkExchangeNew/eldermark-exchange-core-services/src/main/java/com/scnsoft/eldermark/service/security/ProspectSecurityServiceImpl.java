package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityFieldsAware;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ProspectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("prospectSecurityService")
@Transactional(readOnly = true)
public class ProspectSecurityServiceImpl extends BaseSecurityService implements ProspectSecurityService {

    private static Logger logger = LoggerFactory.getLogger(ProspectSecurityServiceImpl.class);

    private final static List<Permission> VIEW_IN_LIST_PERMISSIONS = List.of(
            PROSPECT_VIEW_IN_LIST_ALL,
            PROSPECT_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION,
            PROSPECT_VIEW_IN_LIST_IF_ASSOCIATED_COMMUNITY,
            PROSPECT_VIEW_IN_LIST_IF_CO_REGULAR_CTM
    );

    @Autowired
    private ProspectService prospectService;

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_IN_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long prospectId) {
        return hasPermissions(
                prospectId,
                PROSPECT_VIEW_ALL,
                PROSPECT_VIEW_IF_ASSOCIATED_ORGANIZATION,
                PROSPECT_VIEW_IF_ASSOCIATED_COMMUNITY,
                PROSPECT_VIEW_IF_CO_REGULAR_CTM,
                PROSPECT_VIEW_IF_ADDED_BY_SELF
        );
    }

    @Override
    public boolean canEdit(Long prospectId) {
        return hasPermissions(
                prospectId,
                PROSPECT_EDIT_ALL,
                PROSPECT_EDIT_IF_ASSOCIATED_ORGANIZATION,
                PROSPECT_EDIT_IF_ASSOCIATED_COMMUNITY,
                null,
                PROSPECT_EDIT_IF_ADDED_BY_SELF
        );
    }

    @Override
    public boolean canAdd(ProspectSecurityFieldsAware dto) {
        var permissionFilter = currentUserFilter();

        var communityId = dto.getCommunityId();
        Long organizationId;
        try {
            organizationId = resolveAndValidateOrganizationId(communityId, dto.getOrganizationId(), ANY_TARGET_COMMUNITY);
        } catch (Exception e) {
            logger.warn("Failed to resolve organization", e);
            return false;
        }

        if (!isEligibleForDiscovery(communityId, organizationId, ANY_TARGET_COMMUNITY)) {
            return false;
        }

        if (permissionFilter.hasPermission(PROSPECT_ADD_ALL)) {
            return true;
        }

        if (permissionFilter.hasPermission(PROSPECT_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(PROSPECT_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(PROSPECT_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(PROSPECT_ADD_IF_ASSOCIATED_COMMUNITY);
            if (ANY_TARGET_COMMUNITY.equals(communityId)) {
                if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                    return true;
                }
            } else if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermissions(
            Long prospectId,
            Permission allPermission,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission ifCoRegularCtmPermission,
            Permission ifAddedBySelfPermission
    ) {
        var permissionFilter = currentUserFilter();

        var prospect = prospectService.findById(prospectId, ProspectSecurityAwareEntity.class);

        if (!isEligibleForDiscoveryCommunity(prospect.getCommunityId())) {
            return false;
        }

        if (permissionFilter.hasPermission(allPermission)) {
            return true;
        }

        if (permissionFilter.hasPermission(ifAssociatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganizationPermission);

            if (isAnyCreatedUnderOrganization(employees, prospect.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifAssociatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedCommunityPermission);

            if (isAnyCreatedUnderOrganization(employees, prospect.getCommunityId())) {
                return true;
            }
        }

        // TODO prospect care team implement
        //if (ifCoRegularCtmPermission != null && permissionFilter.hasPermission(ifCoRegularCtmPermission)) {
        //}

        if (permissionFilter.hasPermission(ifAddedBySelfPermission)) {
            var employees = permissionFilter.getEmployees(ifAddedBySelfPermission);

            if (isProspectAddedBySelf(employees, prospect)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEditSsn(Long prospectId) {
        //TODO add security
        return true;
    }
}
