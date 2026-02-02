package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.DocumentSignatureTemplateFileAware;
import com.scnsoft.eldermark.beans.projection.DocumentSignatureTemplateSecurityFieldsAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus;
import com.scnsoft.eldermark.service.security.BaseSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("documentSignatureTemplateSecurityService")
public class DocumentSignatureTemplateSecurityServiceImpl extends BaseSecurityService implements DocumentSignatureTemplateSecurityService {

    private static final List<Permission> VIEW_PERMISSIONS = List.of(
            ROLE_SUPER_ADMINISTRATOR,
            DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_ORGANIZATION,
            DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_COMMUNITY,
            DOCUMENT_TEMPLATE_VIEW_IF_CO_RP_COMMUNITY_CTM,
            DOCUMENT_TEMPLATE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
            DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_RP_CLIENT_CTM,
            DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_REGULAR_CLIENT_CTM
    );

    @Autowired
    private DocumentSignatureTemplateService documentSignatureTemplateService;

    @Override
    public boolean canViewList(Long communityId) {

        if (!isEligibleForDiscoveryCommunity(communityId) || !isSignatureEnabledForCommunity(communityId)) {
            return false;
        }

        return hasAnyPermission(VIEW_PERMISSIONS);
    }

    @Override
    public boolean canViewList(List<Long> communityIds) {
        return communityIds.stream().allMatch(this::canViewList);
    }

    @Override
    public boolean canView(Long id) {
        var template = documentSignatureTemplateService.findById(id);
        return canView(template);
    }

    @Override
    public boolean canView(Long id, Long communityId) {
        var template = documentSignatureTemplateService.findByIdAndCommunityId(id, communityId);
        return canView(template);
    }

    private boolean canView(DocumentSignatureTemplate template) {

        if (template.getStatus() == DocumentSignatureTemplateStatus.DELETED) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var communityIds = Lazy.of(() -> Stream.concat(
                communityService.findCommunityIdsByOrgIds(template.getOrganizationIds()).stream(),
                template.getCommunityIds().stream()
        ).collect(Collectors.toSet()));

        var organizationIds = lazyOrganizationIdsSet(() -> communityService.findAllById(communityIds.get(), OrganizationIdAware.class));

        if (template.getStatus() == DocumentSignatureTemplateStatus.DRAFT) {
            if (filter.hasPermission(DOCUMENT_TEMPLATE_MODIFY_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = filter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_ORGANIZATION);
                if (!isAnyCreatedUnderAnyOrganization(employees, organizationIds.get())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderAnyOrganization(employees, organizationIds.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderAnyCommunity(employees, communityIds.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CO_RP_COMMUNITY_CTM)) {
            if (isAnyInAnyCommunityCareTeam(
                    filter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CO_RP_COMMUNITY_CTM),
                    communityIds.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM)) {
            if (isAnyInAnyCommunityCareTeam(
                    filter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM),
                    communityIds.get(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_RP_CLIENT_CTM)) {
            if (isAnyInAnyClientCareTeamOfAnyCommunity(
                    filter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_RP_CLIENT_CTM),
                    communityIds.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            if (isAnyInAnyClientCareTeamOfAnyCommunity(
                    filter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_REGULAR_CLIENT_CTM),
                    communityIds.get(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long organizationId) {
        if (!isSignatureEnabledForOrganization(organizationId)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_TEMPLATE_ADD_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEdit(Long id) {
        return canEdit(documentSignatureTemplateService.findById(id));
    }

    @Override
    public boolean canEdit(DocumentSignatureTemplate template) {
        return canModify(template);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDelete(Long id) {
        return canDelete(documentSignatureTemplateService.findById(id));
    }

    @Override
    public boolean canDelete(DocumentSignatureTemplate template) {
        return canModify(template);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAssign(Long id) {
        return canAssign(documentSignatureTemplateService.findById(id, DocumentSignatureTemplateSecurityFieldsAware.class));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCopy(Long id) {
        return canCopy(documentSignatureTemplateService.findById(id, DocumentSignatureTemplateFileAware.class));
    }

    private boolean canAssign(DocumentSignatureTemplateSecurityFieldsAware template) {
        var filter = currentUserFilter();

        if (filter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var communityIds = Lazy.of(() -> Stream.concat(
                communityService.findCommunityIdsByOrgIds(template.getOrganizationIds()).stream(),
                template.getCommunityIds().stream()
        ).collect(Collectors.toSet()));

        var organizationIds = lazyOrganizationIdsSet(() -> communityService.findAllById(communityIds.get(), OrganizationIdAware.class));

        if (filter.hasPermission(DOCUMENT_TEMPLATE_ASSIGN_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_TEMPLATE_ASSIGN_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderAnyOrganization(employees, organizationIds.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_TEMPLATE_ASSIGN_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(DOCUMENT_TEMPLATE_ASSIGN_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderAnyCommunity(employees, communityIds.get())) {
                return true;
            }
        }

        return false;
    }

    private boolean canCopy(DocumentSignatureTemplateFileAware template) {
        if (!Boolean.TRUE.equals(template.getIsManuallyCreated())) {
            return false;
        }

        var filter = currentUserFilter();

        if (!filter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return false;
        }

        return true;
    }

    public boolean canModify(DocumentSignatureTemplate template) {
        if (!Boolean.TRUE.equals(template.getIsManuallyCreated()) || template.getStatus() == DocumentSignatureTemplateStatus.DELETED) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var communityIds = Lazy.of(() -> Stream.concat(
                communityService.findCommunityIdsByOrgIds(template.getOrganizationIds()).stream(),
                template.getCommunityIds().stream()
        ).collect(Collectors.toSet()));

        var organizationIds = lazyOrganizationIdsSet(() -> communityService.findAllById(communityIds.get(), OrganizationIdAware.class));

        if (filter.hasPermission(DOCUMENT_TEMPLATE_MODIFY_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_TEMPLATE_MODIFY_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderAnyOrganization(employees, organizationIds.get())) {
                return true;
            }
        }

        return false;
    }

    private boolean isSignatureEnabledForCommunity(Long communityId) {
        var community = communityService.findById(communityId, CommunityIsSignatureEnabledAware.class);
        return community != null && community.getOrganizationIsSignatureEnabled();
    }

    private interface CommunityIsSignatureEnabledAware {
        boolean getOrganizationIsSignatureEnabled();
    }
}
