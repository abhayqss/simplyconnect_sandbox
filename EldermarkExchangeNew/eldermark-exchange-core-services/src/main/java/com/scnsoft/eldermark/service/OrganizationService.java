package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.OrganizationFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityFilter;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationFilterListItemAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationSecurityAwareEntity;
import com.scnsoft.eldermark.dto.organization.OrganizationFeatures;
import com.scnsoft.eldermark.entity.AffiliatedOrganizationRelationship;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizationService extends ProjectingService<Long> {
    Page<Organization> findAll(String name, PermissionFilter permissionFilter, Pageable pageable);

    List<OrganizationFilterListItemAwareEntity> findForFilter(OrganizationFilter organizationFilter, PermissionFilter permissionFilter);

    <P> List<P> findAvailableForSignatureRequest(PermissionFilter permissionFilter, Class<P> projectionClass);

    List<Long> findAllIds();

    Organization save(Organization organization, Boolean createdAutomatically);

    Organization findById(Long id);

    List<Organization> findByIds(List<Long> ids);

    Organization getOne(Long id);

    Long count(PermissionFilter permissionFilter);

    void uploadOrganizationLogo(Long organizationId, MultipartFile logo, boolean isDeleted);

    Pair<byte[], MediaType> downloadLogo(Long id);

    Boolean existsByOid(String oid);

    Boolean existsByName(String name);

    Boolean existsByCompanyId(String companyId);

    boolean existAccessibleOrganizationsWithLabsEnabled(PermissionFilter permissionFilter);

    OrganizationSecurityAwareEntity findSecurityAware(Long id);

    List<AffiliatedOrganizationRelationship> findAffiliatedOrganizations(long id);

    List<IdNameAware> findAllowedReferralMarketplaceOrganizations(PermissionFilter permissionFilter, Community targetCommunity);

    List<IdNameAware> findChatAccessible(PermissionFilter permissionFilter, ConversationParticipantAccessibilityFilter filter);

    void updateOrganizationFeatures(Long organizationId, OrganizationFeatures features);

    Organization findByAlternativeId(String alternativeId);

    boolean hasEligibleForDiscoveryOrNoVisibleCommunities(Long organizationId);

    boolean hasEligibleForDiscoveryCommunities(Long organizationId);

    boolean existsAccessibleOrganizationsWithAppointmentsEnabled(PermissionFilter permissionFilter);
}
