package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentCategorySecurityFieldsAware;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("documentCategorySecurityService")
@Transactional(readOnly = true)
public class DocumentCategorySecurityServiceImpl extends BaseSecurityService implements DocumentCategorySecurityService {

    @Autowired
    private DocumentCategoryService documentCategoryService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    public boolean canAdd(DocumentCategorySecurityFieldsAware dto) {
        return canManageCategories(dto);
    }

    @Override
    public boolean canEdit(Long id) {
        var aware = documentCategoryService.findById(id, DocumentCategorySecurityFieldsAware.class);
        return canManageCategories(aware);
    }

    @Override
    public boolean canDelete(Long id) {
        var aware = documentCategoryService.findById(id, DocumentCategorySecurityFieldsAware.class);
        if (canManageCategories(aware)) {
            return !documentCategoryService.isUsed(id);
        } else {
            return false;
        }
    }

    private boolean canManageCategories(DocumentCategorySecurityFieldsAware securityFieldsAware) {
        if (!organizationService.hasEligibleForDiscoveryOrNoVisibleCommunities(securityFieldsAware.getOrganizationId())) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_MANAGE_ALL)) {
            return true;
        }

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_MANAGE_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_CATEGORY_MANAGE_IF_ASSOCIATED_ORGANIZATION);
            return isAnyCreatedUnderOrganization(employees, securityFieldsAware.getOrganizationId());
        }

        return false;
    }

    @Override
    public boolean canViewList(Long organizationId) {
        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_CATEGORY_VIEW_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_VIEW_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_CATEGORY_VIEW_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_VIEW_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_CATEGORY_VIEW_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunityOfOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_VIEW_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_CATEGORY_VIEW_IF_CO_RP_COMMUNITY_CTM);
            if (isAnyInAnyCommunityCareTeamOfOrganization(
                    employees,
                    organizationId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_VIEW_IF_CO_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_CATEGORY_VIEW_IF_CO_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeamOfOrganization(
                    employees,
                    organizationId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(DOCUMENT_CATEGORY_VIEW_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_CATEGORY_VIEW_IF_SELF_RECORD);
            return isAnySelfClientRecordOfOrganization(employees, organizationId);
        }

        return false;
    }
}
