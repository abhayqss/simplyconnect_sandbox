package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityCommunityFilter;
import com.scnsoft.eldermark.beans.projection.EligibleForDiscoveryAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.dto.community.CommunitySignatureConfig;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.DeviceType;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityService extends SecurityAwareEntityService<CommunitySecurityAwareEntity, Long>,
        ProjectingService<Long> {
    List<Community> findAllById(Iterable<Long> communityIds);

    <P> List<P> findAllEligibleForDiscovery(Class<P> projectionClass);

    <P> List<P> findByOrgIdForFilter(PermissionFilter permissionFilter, Long organizationId, Class<P> projectionClass);

    <P> List<P> findByOrgIdForFilter(PermissionFilter permissionFilter, Long organizationId, Boolean includeInactive, Class<P> projectionClass);

    Page<Community> findVisibleByOrgId(PermissionFilter permissionFilter, Long organizationId, Pageable pageable);

    Community save(Community community);

    Community findById(Long communityId);

    Community get(Long communityId);

    DeviceType saveDeviceType(DeviceType deviceType);

    Page<DeviceType> findDeviceTypeByCommunityId(Long communityId, Pageable pageable);

    DeviceType findDeviceTypeById(Long deviceTypeId);

    Long countVisible(PermissionFilter permissionFilter, Long organizationId);

    boolean hasVisibleCommunities(PermissionFilter permissionFilter, Long organizationId);

    void uploadCommunityLogo(Long communityId, MultipartFile logo, boolean isDeleted);

    Pair<byte[], MediaType> downloadLogo(Long id);

    Boolean existsByOidInOrganization(Long organizationId, String oid);

    Boolean existsByNameInOrganization(Long organizationId, String name);

    List<Community> findForNonNetworkReferralRequestExceptWithServices(Community community, List<Long> serviceIds);

    List<IdNameAware> findAllByOrgId(Long organizationId);

    List<Long> findCommunityIdsByOrgId(Long organizationId);

    List<Long> findCommunityIdsByOrgIds(Collection<Long> organizationIds);

    boolean isAccessibleReferralMarketplaceCommunity(PermissionFilter permissionFilter, Community marketplaceCommunity);

    //todo move both to referral service
    List<IdNameAware> findAllowedReferralMarketplaceCommunities(PermissionFilter permissionFilter, Long targetCommunityId, Long organizationId);

    boolean existAllowedReferralMarketplaceCommunitiesWithinAnyNetworks(PermissionFilter permissionFilter, Long targetCommunityId, Collection<Long> networkIds);

    List<IdNameAware> findChatAccessible(PermissionFilter permissionFilter, ConversationParticipantAccessibilityCommunityFilter filter);

    boolean isEligibleForDiscovery(EligibleForDiscoveryAware community);

    boolean isEligibleForDiscovery(Long communityId);

    Optional<IdAware> findByHealthPartnersBillingProviderRef(String ref, Long organizationId);

    void updateSignatureConfig(Long id, CommunitySignatureConfig features);

    <P> List<P> findByOrgIdForFilterEnabledInMarketPlace(PermissionFilter permissionFilter, Long organizationId, Class<P> projectionClass);

    <P> List<P> findAvailableForSignatureRequest(Long organizationId, PermissionFilter permissionFilter, Class<P> projectionClass);

    Optional<IdAware> findByOrganizationOidAndCommunityOid(String organizationOid, String communityOid);
}
