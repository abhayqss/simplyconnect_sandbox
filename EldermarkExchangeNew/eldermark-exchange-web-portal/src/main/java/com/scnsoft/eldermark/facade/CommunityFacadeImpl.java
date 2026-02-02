package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.annotations.AuditLog;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityCommunityFilter;
import com.scnsoft.eldermark.beans.projection.IdNameOrganizationIdAware;
import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.directory.DirCommunityListItemDto;
import com.scnsoft.eldermark.dto.docutrack.DocutrackPharmacyConfigDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityPicture;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.community.DeviceType;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.docutrack.DocutrackService;
import com.scnsoft.eldermark.service.security.*;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.DocumentUtils;
import com.scnsoft.eldermark.util.KeyStoreUtil;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class CommunityFacadeImpl implements CommunityFacade {

    private static final Logger logger = LoggerFactory.getLogger(CommunityFacadeImpl.class);

    private final Set<String> ALLOWED_EXTENSIONS = Set.of("JPEG", "GIF", "PNG", "JPG");

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private AutoCloseIntervalService autoCloseIntervalService;

    @Autowired
    private ListAndItemConverter<Community, CommunityListItemDto> communityListItemDtoConverter;

    @Autowired
    private ItemConverter<CommunityDto, Community> communityConverter;

    @Autowired
    private Converter<Community, CommunityDto> communityDtoConverter;

    @Autowired
    private ListAndItemConverter<DeviceType, CommunityDeviceTypeDto> communityDeviceTypeDtoConverter;

    @Autowired
    private ListAndItemConverter<IdNameOrganizationIdAware, DirCommunityListItemDto> directoryCommunityConverter;

    @Autowired
    private Converter<CommunityDeviceTypeDto, DeviceType> communityDeviceTypeEntityConverter;

    @Autowired
    private Converter<Pair<Marketplace, Long>, MarketplaceDto> marketplaceDtoConverter;

    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private CommunityAddressService communityAddressService;

    @Autowired
    private ItemConverter<MarketplaceDto, Marketplace> marketplaceConverter;

    @Autowired
    private CommunitySecurityService communitySecurityService;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @Autowired
    private AffiliatedRelationshipService affiliatedRelationshipService;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityService externalEmployeeInboundReferralCommunityService;

    @Autowired
    private ExternalEmployeeRequestService externalEmployeeRequestService;

    @Autowired
    private Converter<List<ServiceType>, List<ServiceTypeListItemDto>> serviceTypeListItemDtoConverter;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ListAndItemConverter<MultipartFile, CommunityPicture> communityPictureEntityConverter;

    @Autowired
    private CommunityPictureService communityPictureService;

    @Autowired
    private DocutrackSecurityService docutrackSecurityService;

    @Autowired
    private DocutrackService docutrackService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private Converter<FeaturedServiceProviderDto, FeaturedServiceProvider> featuredServiceProviderDtoConverter;

    @Autowired
    private FeaturedServiceProviderService featuredServiceProviderService;

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Autowired
    private CommunityHieConsentPolicySecurityService communityHieConsentPolicySecurityService;

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canViewList()")
    public List<DirCommunityListItemDto> findNonBlankByOrgId(@P("organizationId") Long organizationId, Boolean isMarketplaceEnabledOnly) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        List<IdNameOrganizationIdAware> communities;
        if (BooleanUtils.isTrue(isMarketplaceEnabledOnly)) {
            communities = communityService.findByOrgIdForFilterEnabledInMarketPlace(permissionFilter, organizationId, IdNameOrganizationIdAware.class);
        } else {
            communities = communityService.findByOrgIdForFilter(permissionFilter, organizationId, IdNameOrganizationIdAware.class);
        }

        return directoryCommunityConverter
                .convertList(communities.stream().filter(community -> StringUtils.isNotBlank(community.getName())).collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canViewList()")
    @AuditLog
    public Page<CommunityListItemDto> findByOrgId(@P("organizationId") Long organizationId, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var communities = communityService.findVisibleByOrgId(
                permissionFilter,
                organizationId,
                PaginationUtils.sortByDefault(pageable, Sort.by(Community_.NAME))
        );
        return communities.map(communityListItemDtoConverter::convert);
    }

    @Override
    @Transactional
    @PreAuthorize("@communitySecurityService.canAdd(#communityDto)")
    public Long add(@P("communityDto") CommunityDto communityDto) {
        return save(communityDto);
    }

    @Override
    @Transactional
    @PreAuthorize("@communitySecurityService.canEdit(#communityDto.id)")
    public Long edit(@P("communityDto") CommunityDto communityDto) {
        return save(communityDto);
    }

    private Long save(CommunityDto communityDto) {
        logger.debug("Request Body : {}", communityDto);

        validateBusinessUnitCodes(communityDto);

        communityDto.setPictureFiles(validateAndReduceCommunityPictures(communityDto.getPictureFiles()));

        var community = communityConverter.convert(communityDto);

        community = communityService.save(community);

        saveHieConsentPolicy(communityDto.getHieConsentPolicyName(), community);

        saveMarketplace(communityDto.getMarketplace(), community);

        saveFeaturedServiceProviders(communityDto.getFeaturedServiceProviders(), community);

        updateDocutrackCertificate(communityDto.getDocutrackPharmacyConfig(), community);

        if (communityDto.getLogo() != null || communityDto.isShouldRemoveLogo()) {
            communityService.uploadCommunityLogo(community.getId(), communityDto.getLogo(),
                    communityDto.isShouldRemoveLogo());
        }

        if (communityDto.getId() != null) {
            communityPictureService.deleteWithContent(community.getPictures());
        }
        var pictures = communityPictureEntityConverter.convertList(communityDto.getPictureFiles());
        for (var picture : pictures) {
            picture.setCommunity(community);
            communityPictureService.saveWithContent(picture);
        }

        if (communitySecurityService.canEditSignatureConfig(community.getId())) {
            validationService.validate(communityDto, ValidationGroups.CommunitySignatureConfig.class);
            communityService.updateSignatureConfig(community.getId(), communityDto.getSignatureConfig());
        }

        return community.getId();
    }

    private void saveHieConsentPolicy(HieConsentPolicyType policy, Community community) {
        if (communityHieConsentPolicySecurityService.canEdit(community.getId())) {
            if (policy == null) {
                throw new ValidationException("hieConsentPolicy cannot be null");
            }
            hieConsentPolicyUpdateService.updateCommunityDefaultHieConsentPolicy(community, policy, HieConsentPolicySource.WEB, loggedUserService.getCurrentEmployee());
        } else {
            communityHieConsentPolicyService.createDefaultStatePolicyIfNotExist(community);
        }
    }

    private void validateBusinessUnitCodes(CommunityDto communityDto) {
        var config = communityDto.getDocutrackPharmacyConfig();
        if (shouldProcessDocutrack(config, communityDto.getOrganizationId())) {
            var duplicateCodes = docutrackService.nonUniqueBusinessUnitCodes(
                    config.getServerDomain(),
                    communityDto.getId(),
                    config.getBusinessUnitCodes()
            );

            if (CollectionUtils.isNotEmpty(duplicateCodes)) {
                throw new ValidationException("Business unit codes should be unique within server domain: " + duplicateCodes);
            }
        }
    }

    private void validateReferralEmails(MarketplaceDto marketplaceDto) {
        var referralEmails = marketplaceDto.getReferralEmails();
        if (CollectionUtils.isEmpty(referralEmails)) {
            throw new ValidationException("Referral emails must not be empty");
        }
        var uniqueEmails = referralEmails.stream().map(String::toLowerCase).collect(Collectors.toSet());
        if (uniqueEmails.size() != referralEmails.size()) {
            throw new ValidationException("Referral emails should be unique");
        }
    }

    private List<MultipartFile> validateAndReduceCommunityPictures(List<MultipartFile> pictures) {
        return Stream.ofNullable(pictures)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .peek(file -> DocumentUtils.validateUploadedFile(file, ALLOWED_EXTENSIONS))
                .collect(Collectors.toList());
    }

    private void saveMarketplace(MarketplaceDto marketplaceDto, Community community) {
        Marketplace marketplace;
        if (marketplaceDto.getId() != null) {
            marketplace = marketplaceService.findById(marketplaceDto.getId());
            marketplaceConverter.convert(marketplaceDto, marketplace);
        } else {
            marketplace = Objects.requireNonNull(marketplaceConverter.convert(marketplaceDto));
            marketplace.setOrganizationId(community.getOrganizationId());
            marketplace.setOrganization(community.getOrganization());
            marketplace.setCommunity(community);
            marketplace.setCommunityId(community.getId());
        }
        if (marketplaceCommunitySecurityService.canConfigure(community.getId())) {
            marketplace.setDiscoverable(marketplaceDto.getConfirmVisibility());

            validateReferralEmails(marketplaceDto);
            marketplace.setReferralEmails(marketplaceDto.getReferralEmails());
            updateExternalEmployeeInboundReferralCommunity(marketplaceDto.getReferralEmails(), community);
        } else if (marketplace.getId() == null) {
            marketplace.setDiscoverable(MarketplaceService.DEFAULT_DISCOVERABLE);
        }

        marketplaceService.save(marketplace);
        communityAddressService.populateAllLocationForOutdatedAddresses(community.getOrganizationId());
    }

    private void updateDocutrackCertificate(DocutrackPharmacyConfigDto config, Community community) {
        try {
            if (shouldProcessDocutrack(config, community.getOrganizationId())) {
                if (config.isShouldRemoveCertificate()) {
                    logger.info("Removing docutrack certificate for community [{}]", community.getId());
                    docutrackService.updateServerCertificate(community, (X509Certificate) null);
                } else if (config.getPublicKeyCertificate() != null) {
                    logger.info("Setting up docutrack certificate for community [{}] from file", community.getId());
                    docutrackService.updateServerCertificate(community, config.getPublicKeyCertificate().getBytes());
                } else if (StringUtils.isNotEmpty(config.getAcceptedCertificateSha1Fingerprint()) &&
                        (community.getDocutrackServerCertificateSha1() == null ||
                                !config.getAcceptedCertificateSha1Fingerprint().equalsIgnoreCase(new String(Hex.encode(community.getDocutrackServerCertificateSha1()))))) {
                    logger.info("Setting up accepted docutrack certificate for community [{}]", community.getId());
                    var cert = docutrackService.loadServerCertIfSelfSigned(community.getDocutrackServerDomain())
                            .orElseThrow(() -> new BusinessException("Couldn't fetch self-signed certificate from server domain"));
                    if (config.getAcceptedCertificateSha1Fingerprint().equalsIgnoreCase(KeyStoreUtil.sha1HexFingerprint(cert))) {
                        docutrackService.updateServerCertificate(community, cert);
                    } else {
                        throw new BusinessException("Accepted certificate doesn't match with server certificate");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error during docutrack certificate upload");
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR);
        }
    }

    private boolean shouldProcessDocutrack(DocutrackPharmacyConfigDto config, Long organizationId) {
        return config != null
                && config.getIsIntegrationEnabled()
                && docutrackSecurityService.canConfigureDocutrackInOrg(organizationId);
    }

    private void updateExternalEmployeeInboundReferralCommunity(List<String> referralEmails, Community community) {
        var externalEmployees = externalEmployeeInboundReferralCommunityService.findAllByCommunityId(community.getId());
        addExistingExternalEmployeeToInboundReferralCommunity(referralEmails, externalEmployees, community);
        deleteExternalEmployeeFromInboundReferralCommunity(referralEmails, externalEmployees, community);
    }

    private void addExistingExternalEmployeeToInboundReferralCommunity(List<String> referralEmails, List<ExternalEmployeeInboundReferralCommunity> externalEmployees, Community community) {
        var externalEmployeeLoginNames = StreamUtils.stream(externalEmployees)
                .map(ExternalEmployeeInboundReferralCommunity::getEmployee)
                .map(Employee::getLoginName)
                .collect(Collectors.toList());
        var newReferralEmails = StreamUtils.stream(referralEmails)
                .filter(re -> !externalEmployeeLoginNames.contains(re))
                .collect(Collectors.toSet());
        var existedExternalEmployees = employeeService.findAllExternalEmployees(newReferralEmails, EmployeeStatus.ACTIVE);
        existedExternalEmployees.forEach(em -> externalEmployeeInboundReferralCommunityService.create(em, community));
    }

    private void deleteExternalEmployeeFromInboundReferralCommunity(List<String> referralEmails, List<ExternalEmployeeInboundReferralCommunity> externalEmployees, Community community) {
        var externalEmployeesForDeleting = StreamUtils.stream(externalEmployees)
                .filter(em -> !referralEmails.contains(em.getEmployee().getLoginName()))
                .collect(Collectors.toList());
        var externalEmployeeLoginNamesForDeleting = StreamUtils.stream(externalEmployeesForDeleting)
                .map(ExternalEmployeeInboundReferralCommunity::getEmployee)
                .map(Employee::getLoginName)
                .collect(Collectors.toSet());
        externalEmployeeRequestService.deleteAllByExternalEmployeeLoginNamesAndCommunityId(externalEmployeeLoginNamesForDeleting, community.getId());
        externalEmployeeInboundReferralCommunityService.deleteAll(externalEmployeesForDeleting);
    }

    private void saveFeaturedServiceProviders(List<FeaturedServiceProviderDto> featuredServiceProviders, Community community) {

        if (marketplaceCommunitySecurityService.canEditFeaturedPartnerProviders(community.getId(), community.getOrganizationId())) {
            if (CollectionUtils.isNotEmpty(featuredServiceProviders)) {
                var existedProviders =
                        featuredServiceProviderService.findAllByCommunityIdAndProviderIdIn(
                                community.getId(), featuredServiceProviders.stream()
                                        .map(FeaturedServiceProviderDto::getCommunityId)
                                        .collect(Collectors.toList())
                        );
                var existedProviderIds = existedProviders.stream()
                        .map(FeaturedServiceProvider::getProviderId)
                        .collect(Collectors.toList());

                existedProviders.forEach(provider ->
                        provider.setDisplayOrder(
                                featuredServiceProviders.stream()
                                        .filter(dto -> dto.getCommunityId().equals(provider.getProviderId()))
                                        .findFirst()
                                        .map(FeaturedServiceProviderDto::getDisplayOrder)
                                        .orElse(null)
                        )
                );
                var newProviders =
                        featuredServiceProviders.stream()
                                .filter(dto -> !existedProviderIds.contains(dto.getCommunityId()))
                                .map(featuredServiceProviderDtoConverter::convert)
                                .peek(provider -> {
                                    provider.setCommunityId(community.getId());
                                    provider.setCommunity(community);
                                })
                                .collect(Collectors.toList());

                existedProviders.addAll(newProviders);
                featuredServiceProviderService.saveAll(existedProviders);

                removeOldProviders(community.getId(), featuredServiceProviders);
            }
        }
    }

    private void removeOldProviders(Long communityId, List<FeaturedServiceProviderDto> featuredServiceProviders) {
        var providerIdsToRemove = featuredServiceProviders.stream()
                .filter(dto -> Objects.isNull(dto.getDisplayOrder()))
                .map(FeaturedServiceProviderDto::getCommunityId)
                .collect(Collectors.toList());
        featuredServiceProviderService.deleteByCommunityIdAndProviderIdIn(communityId, providerIdsToRemove);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canView(#communityId)")
    public CommunityDto findById(@P("communityId") Long communityId, Boolean marketplaceDataIncluded) {
        var community = communityService.findById(communityId);
        var communityDto = communityDtoConverter.convert(community);
        if (BooleanUtils.isNotFalse(marketplaceDataIncluded)) {
            var marketplace = marketplaceService.findByCommunityId(communityId);
            if (marketplace != null) {
                communityDto.setMarketplace(marketplaceDtoConverter.convert(Pair.of(marketplace, null)));
            }
        }
        return communityDto;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canView(#id)")
    public boolean isExistsAffiliated(@P("id") Long id) {
        return affiliatedRelationshipService.existsByPrimaryCommunityIdIn(Set.of(id));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canView(#communityId)")
    public Page<CommunityDeviceTypeDto> findDeviceTypeByCommunityId(@P("communityId") Long communityId,
                                                                    Pageable pageable) {
        var deviceTypeListItems = communityService.findDeviceTypeByCommunityId(communityId, pageable);
        return new PageImpl<>(communityDeviceTypeDtoConverter.convertList(deviceTypeListItems.getContent()), pageable,
                deviceTypeListItems.getTotalElements());
    }

    @Override
    @Transactional
    @PreAuthorize("@communitySecurityService.canEdit(#communityId)")
    public Long saveDeviceType(@P("communityId") Long communityId, CommunityDeviceTypeDto communityDeviceTypeDto) {
        logger.info("Request Body :" + communityDeviceTypeDto.toString());
        var deviceType = communityDeviceTypeEntityConverter.convert(communityDeviceTypeDto);
        deviceType.setAutoCloseInterval(
                autoCloseIntervalService.findById(communityDeviceTypeDto.getAutoCloseIntervalId()));
        deviceType.setCommunity(communityService.findById(communityId));
        return communityService.saveDeviceType(deviceType).getId();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canViewByDeviceTypeId(#deviceTypeId)")
    public CommunityDeviceTypeDto findDeviceTypeById(@P("deviceTypeId") Long deviceTypeId) {
        var deviceType = communityService.findDeviceTypeById(deviceTypeId);
        return communityDeviceTypeDtoConverter.convert(deviceType);
    }

    @Override
    @PreAuthorize("@communitySecurityService.canViewList()")
    @Transactional(readOnly = true)
    public Long count(@P("organizationId") Long organizationId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return communityService.countVisible(permissionFilter, organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long organizationId) {
        return communitySecurityService.canAdd(() -> organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canViewLogo(#communityId)")
    public FileBytesDto downloadLogo(Long communityId) {
        Optional<Pair<byte[], MediaType>> bytesWithMediaType = Optional.ofNullable(communityService.downloadLogo(communityId));
        return bytesWithMediaType.map(mediaTypePair -> new FileBytesDto(mediaTypePair.getFirst(), mediaTypePair.getSecond())).orElse(new FileBytesDto());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canAdd(new com.scnsoft.eldermark.beans.security.projection.dto.CommunitySecurityFieldsAwareImpl(#organizationId))")
    public CommunityUniquenessDto validateUniqueFields(@P("organizationId") Long organizationId, String oid, String name) {
        Boolean oidValid = oid != null ? !communityService.existsByOidInOrganization(organizationId, oid) : null;
        Boolean nameValid = name != null ? !communityService.existsByNameInOrganization(organizationId, name) : null;
        return new CommunityUniquenessDto(oidValid, nameValid);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canViewList()")
    public List<IdentifiedTitledEntityDto> findChatAccessible(ConversationParticipantAccessibilityCommunityFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        return communityService.findChatAccessible(permissionFilter, filter).stream()
                .map(community -> new IdentifiedTitledEntityDto(community.getId(), community.getName())).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canDownloadPicture(#pictureId)")
    public FileBytesDto downloadPictureById(@P("pictureId") Long pictureId) {
        var attachment = communityPictureService.findByIdWithContent(pictureId);
        return new FileBytesDto(attachment.getContent(), attachment.getMimeType() != null ? MediaType.valueOf(attachment.getMimeType()) : null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@communitySecurityService.canView(#communityId) or " +
            "@marketplaceCommunitySecurityService.canViewByCommunityId(#communityId)")
    public List<ServiceTypeListItemDto> getServices(Long communityId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var services = serviceTypeService.findAllowedForReferralByCommunityId(permissionFilter, communityId);
        return serviceTypeListItemDtoConverter.convert(services);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityPermissionsDto getPermissions(Long organizationId) {
        var permissions = new CommunityPermissionsDto();
        permissions.setCanAdd(communitySecurityService.canAdd(() -> organizationId));
        permissions.setCanEditDocutrack(docutrackSecurityService.canConfigureDocutrackInOrg(organizationId));
        permissions.setCanEditHieConsentPolicy(communityHieConsentPolicySecurityService.canEdit(null));
        permissions.setCanEditSignatureSetup(communitySecurityService.canEditSignatureConfigInOrganization(organizationId));
        var canConfigureMarketplace = marketplaceCommunitySecurityService.canConfigureInOrganization(organizationId);
        permissions.setCanEditAllowExternalInboundReferrals(canConfigureMarketplace);
        permissions.setCanEditMarketplaceReferralEmails(canConfigureMarketplace);
        permissions.setCanEditConfirmMarketplaceVisibility(canConfigureMarketplace);
        permissions.setCanEditFeaturedServiceProviders(marketplaceCommunitySecurityService.canEditFeaturedPartnerProviders(null, organizationId));
        return permissions;
    }
}
