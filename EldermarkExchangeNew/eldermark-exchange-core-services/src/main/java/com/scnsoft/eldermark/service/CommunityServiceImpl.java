package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityCommunityFilter;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.DeviceTypeDao;
import com.scnsoft.eldermark.dao.specification.CommunitySpecificationGenerator;
import com.scnsoft.eldermark.dto.community.CommunitySignatureConfig;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.community.DeviceType;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.storage.ImageFileStorage;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private static final Logger logger = LoggerFactory.getLogger(CommunityServiceImpl.class);

    private final CommunityDao communityDao;
    private final CommunitySpecificationGenerator communitySpecificationGenerator;
    private final DeviceTypeDao deviceTypeDao;
    private final ImageFileStorage imageFileStorage;
    private final DocumentSignatureRequestService signatureRequestService;
    private final LoggedUserService loggedUserService;
    private final DocumentFolderService documentFolderService;

    //this done to resolve circular dependencies
    public CommunityServiceImpl(
            CommunityDao communityDao,
            CommunitySpecificationGenerator communitySpecificationGenerator,
            DeviceTypeDao deviceTypeDao,
            ImageFileStorage imageFileStorage,
            @Lazy DocumentSignatureRequestService signatureRequestService,
            LoggedUserService loggedUserService,
            @Lazy DocumentFolderService documentFolderService
    ) {
        this.communityDao = communityDao;
        this.communitySpecificationGenerator = communitySpecificationGenerator;
        this.deviceTypeDao = deviceTypeDao;
        this.imageFileStorage = imageFileStorage;
        this.signatureRequestService = signatureRequestService;
        this.loggedUserService = loggedUserService;
        this.documentFolderService = documentFolderService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Community> findAllById(Iterable<Long> communityIds) {
        return communityDao.findAllById(communityIds);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllEligibleForDiscovery(Class<P> projectionClass) {
        return communityDao.findAll(communitySpecificationGenerator.eligibleForDiscovery(), projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findByOrgIdForFilter(PermissionFilter permissionFilter, Long organizationId, Class<P> projectionClass) {
        return findByOrgIdForFilter(permissionFilter, organizationId, false, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findByOrgIdForFilter(PermissionFilter permissionFilter, Long organizationId, Boolean includeInactive, Class<P> projectionClass) {
        var byOrganizationIdEligibleForDiscovery = communitySpecificationGenerator
                .byOrganizationIdEligibleForDiscovery(organizationId, includeInactive);
        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);

        return communityDao.findAll(byOrganizationIdEligibleForDiscovery.and(hasAccess),
                projectionClass,
                Sort.by(Direction.ASC, Community_.NAME)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Community> findVisibleByOrgId(PermissionFilter permissionFilter, Long organizationId, Pageable pageable) {
        var byOrganizationId = communitySpecificationGenerator.byOrganizationId(organizationId);
        var isVisible = communitySpecificationGenerator.isVisible();
        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);

        return communityDao.findAll(byOrganizationId.and(isVisible.and(hasAccess)), pageable);
    }

    @Override
    @Transactional
    public Community save(Community community) {
        checkUniqueValues(community);
        boolean existed = community.getId() != null;
        community = communityDao.saveAndFlush(community);

        // todo use CareCoordinationConstants.updateLegacyId?
        if (!existed) {
            var legacyId = Long.toString(community.getId());
            community.setLegacyId(legacyId);

            if (CollectionUtils.isNotEmpty(community.getAddresses())) {
                community.getAddresses().forEach(communityAddress -> communityAddress.setLegacyId(legacyId));
            }

            community = communityDao.save(community);

            documentFolderService.createTemplateFolder(community.getId());
        }

        return community;
    }

    private void checkUniqueValues(Community community) {
        if (community.getOid() != null && (community.getId() != null
                ? communityDao.existsByOrganizationIdAndOidAndIdNot(community.getOrganizationId(), community.getOid(), community.getId())
                : communityDao.existsByOrganizationIdAndOid(community.getOrganizationId(), community.getOid()))) {
            throw new BusinessException("Community OID already exists in organization");
        }

        if (community.getId() != null ? communityDao.existsByOrganizationIdAndNameAndIdNot(community.getOrganizationId(), community.getName(), community.getId())
                : communityDao.existsByOrganizationIdAndName(community.getOrganizationId(), community.getName())) {
            throw new BusinessException("Community name already exists in organization");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Community findById(Long communityId) {
        return communityDao.findById(communityId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Community get(Long communityId) {
        return communityDao.getOne(communityId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceType> findDeviceTypeByCommunityId(Long communityId, Pageable pageable) {
        return deviceTypeDao.findAllByCommunity_Id(communityId, pageable);
    }

    @Override
    @Transactional
    public DeviceType saveDeviceType(DeviceType deviceType) {
        return deviceTypeDao.save(deviceType);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceType findDeviceTypeById(Long deviceTypeId) {
        return deviceTypeDao.findById(deviceTypeId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countVisible(PermissionFilter permissionFilter, Long organizationId) {
        var byOrganizationId = communitySpecificationGenerator.byOrganizationId(organizationId);
        var isVisible = communitySpecificationGenerator.isVisible();
        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);

        return communityDao.count(byOrganizationId.and(isVisible.and(hasAccess)));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVisibleCommunities(PermissionFilter permissionFilter, Long organizationId) {
        var byOrganizationId = communitySpecificationGenerator.byOrganizationId(organizationId);
        var isVisible = communitySpecificationGenerator.isVisible();
        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);

        return communityDao.exists(byOrganizationId.and(isVisible.and(hasAccess)));
    }

    @Override
    @Transactional
    public void uploadCommunityLogo(Long communityId, MultipartFile logo, boolean isDeletedLogo) {
        Community community = communityDao.findById(communityId).orElseThrow();
        if (isDeletedLogo) {
            deleteCommunityLogo(community);
        } else {
            if (StringUtils.isNotEmpty(community.getMainLogoPath())) {
                imageFileStorage.delete(community.getMainLogoPath());
            }
            String ext = FilenameUtils.getExtension(logo.getOriginalFilename());
            String mainLogoPath = "logo_comm_" + communityId + "_" + System.currentTimeMillis() + "." + ext;
            try (var inputStream = logo.getInputStream()) {
                imageFileStorage.save(inputStream, mainLogoPath);
            } catch (IOException e) {
                logger.error("Error during image upload", e);
                throw new BusinessException(
                        "There were some error while uploading logo for community id=" + communityId);
            }
            community.setMainLogoPath(mainLogoPath);
            communityDao.save(community);
        }
    }

    @Override
    public Pair<byte[], MediaType> downloadLogo(Long id) {
        var community = communityDao.findById(id, MainLogoPathAware.class).orElseThrow();
        if (imageFileStorage.exists(community.getMainLogoPath())) {
            return imageFileStorage.loadAsBytesWithMediaType(community.getMainLogoPath());
        } else {
            return null;
        }
    }

    private void deleteCommunityLogo(Community community) {
        if (community.getMainLogoPath() != null) {
            imageFileStorage.delete(community.getMainLogoPath());
            community.setMainLogoPath(null);
            communityDao.save(community);
        }
    }

    @Override
    public Boolean existsByOidInOrganization(Long organizationId, String oid) {
        return communityDao.existsByOrganizationIdAndOid(organizationId, oid);
    }

    @Override
    public Boolean existsByNameInOrganization(Long organizationId, String name) {
        return communityDao.existsByOrganizationIdAndName(organizationId, name);
    }

    @Override
    public List<Community> findForNonNetworkReferralRequestExceptWithServices(Community community, List<Long> serviceIds) {
        var withEnabledFlag = communitySpecificationGenerator.withNonNetworkFlagEnabledEligibleForDiscovery();
        var notEqual = communitySpecificationGenerator.notEqual(community);
        var spec = withEnabledFlag.and(notEqual);
        if (CollectionUtils.isNotEmpty(serviceIds)) {
            spec = spec.and(communitySpecificationGenerator.withServices(serviceIds));
        }
        return communityDao.findAll(spec);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNameAware> findAllByOrgId(Long organizationId) {
        var byOrganizationId = communitySpecificationGenerator.byOrganizationIdEligibleForDiscovery(organizationId);
        return communityDao.findAll(byOrganizationId, IdNameAware.class, Sort.by(Direction.ASC, Community_.NAME));
    }

    @Override
    @Transactional(readOnly = true)
    public CommunitySecurityAwareEntity findSecurityAwareEntity(Long id) {
        return communityDao.findById(id, CommunitySecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunitySecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return communityDao.findByIdIn(ids, CommunitySecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findCommunityIdsByOrgId(Long organizationId) {
        var byOrganizationId = communitySpecificationGenerator.byOrganizationIdEligibleForDiscovery(organizationId);
        return communityDao.findAll(byOrganizationId, IdAware.class).stream()
                .map(IdAware::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findCommunityIdsByOrgIds(Collection<Long> organizationIds) {
        var byOrganizationId = communitySpecificationGenerator.byOrganizationIdsEligibleForDiscovery(organizationIds);
        return communityDao.findAll(byOrganizationId, IdAware.class).stream()
            .map(IdAware::getId)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return communityDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return communityDao.findByIdIn(ids, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccessibleReferralMarketplaceCommunity(PermissionFilter permissionFilter, Community marketplaceCommunity) {
        var allowedCommunities = communitySpecificationGenerator.communitiesInReferralMarketplaceAllowedCommunities(permissionFilter, marketplaceCommunity);
        return communityDao.exists(allowedCommunities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNameAware> findAllowedReferralMarketplaceCommunities(PermissionFilter permissionFilter, Long targetCommunityId, Long organizationId) {
        var byOrganizationId = communitySpecificationGenerator.byOrganizationIdEligibleForDiscovery(organizationId);
        var allowedCommunities = communitySpecificationGenerator.communitiesInReferralMarketplaceAllowedCommunities(permissionFilter, communityDao.getOne(targetCommunityId));
        return communityDao.findAll(byOrganizationId.and(allowedCommunities), IdNameAware.class, Sort.by(Direction.ASC, Community_.NAME));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existAllowedReferralMarketplaceCommunitiesWithinAnyNetworks(PermissionFilter permissionFilter, Long targetCommunityId, Collection<Long> networkIds) {
        var allowedCommunities = communitySpecificationGenerator.communitiesInReferralMarketplaceAllowedCommunities(permissionFilter, communityDao.getOne(targetCommunityId));
        var withinNetworks = communitySpecificationGenerator.withinNetworksEligibleForDiscovery(networkIds);
        return communityDao.exists(withinNetworks.and(allowedCommunities));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNameAware> findChatAccessible(PermissionFilter permissionFilter,
                                                ConversationParticipantAccessibilityCommunityFilter filter) {
        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);
        var byFilter = communitySpecificationGenerator.byAccessibleChatCommunityFilter(permissionFilter, filter);
        return communityDao.findAll(hasAccess.and(byFilter), IdNameAware.class, Sort.by(Direction.ASC, Community_.NAME));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEligibleForDiscovery(EligibleForDiscoveryAware community) {
        return isVisible(community) && Boolean.TRUE.equals(community.getModuleHie());
    }

    private boolean isVisible(VisibleCommunityFieldsAware community) {
        if (community == null) {
            return false;
        }
        if (Boolean.TRUE.equals(community.getInactive())) {
            return false;
        }
        if (Boolean.TRUE.equals(community.getTestingTraining())) {
            return false;
        }
        return CareCoordinationConstants.COMMUNITY_ELIGIBLE_FOR_DISCOVERY_LEGACY_TABLE
                .equalsIgnoreCase(community.getLegacyTable());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEligibleForDiscovery(Long communityId) {
        return communityId != null && isEligibleForDiscovery(
                communityDao.findById(communityId, EligibleForDiscoveryAware.class).orElse(null)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdAware> findByHealthPartnersBillingProviderRef(String ref, Long organizationId) {
        return communityDao.findFirst((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Community_.healthPartnersBillingProviderRef), ref),
                criteriaBuilder.equal(root.get(Community_.organizationId), organizationId)
        ), IdAware.class);
    }

    @Override
    @Transactional
    public void updateSignatureConfig(Long id, CommunitySignatureConfig config) {
        var community = communityDao.findById(id).orElseThrow();

        if (!Objects.equals(config.getIsPinEnabled(), community.getIsSignaturePinEnabled())) {
            var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
            signatureRequestService.cancelRequestedByCommunityIdAsync(community.getId(), currentEmployeeId);
        }

        community.setIsSignaturePinEnabled(config.getIsPinEnabled());
        communityDao.save(community);
    }

    @Transactional(readOnly = true)
    public <P> List<P> findByOrgIdForFilterEnabledInMarketPlace(PermissionFilter permissionFilter, Long organizationId, Class<P> projectionClass) {
        var byOrganizationIdEligibleForDiscovery = communitySpecificationGenerator.byOrganizationIdEligibleForDiscovery(organizationId);
        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);
        var enabledInMarketPlace = communitySpecificationGenerator.enabledInMarketplace();
        return communityDao.findAll(byOrganizationIdEligibleForDiscovery.and(hasAccess.and(enabledInMarketPlace)),
                projectionClass,
                Sort.by(Direction.ASC, Community_.NAME)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAvailableForSignatureRequest(Long organizationId, PermissionFilter permissionFilter, Class<P> projectionClass) {
        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);
        var eligibleForDiscovery = communitySpecificationGenerator.byOrganizationIdEligibleForDiscovery(organizationId);
        var hasClientsForSignatureRequest = communitySpecificationGenerator.hasClientsAvailableForSignatureRequest(permissionFilter);

        var spec = hasAccess
                .and(eligibleForDiscovery)
                .and(hasClientsForSignatureRequest);

        return communityDao.findAll(spec, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdAware> findByOrganizationOidAndCommunityOid(String organizationOid, String communityOid) {
        var byOid = communitySpecificationGenerator.byOid(communityOid);
        var byOrganizationOid = communitySpecificationGenerator.byOrganizationOid(organizationOid);
        return communityDao.findFirst(byOid.and(byOrganizationOid), IdAware.class);
    }
}
