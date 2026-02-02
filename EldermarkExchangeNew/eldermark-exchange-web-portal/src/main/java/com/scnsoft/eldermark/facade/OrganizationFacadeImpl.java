package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.OrganizationFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationFilterListItemAwareEntity;
import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.directory.DirOrganizationListItemDto;
import com.scnsoft.eldermark.dto.organization.OrganizationFeatures;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.OrganizationSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class OrganizationFacadeImpl implements OrganizationFacade {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationFacadeImpl.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private Converter<Organization, OrganizationListItemDto> organizationListItemDtoConverter;

    @Autowired
    private Converter<OrganizationDto, Organization> organizationConverter;

    @Autowired
    private Converter<Organization, OrganizationDto> organizationDtoConverter;

    @Autowired
    private ItemConverter<Organization, OrganizationBaseDto> organizationBasicDtoItemConverter;

    @Autowired
    private ListAndItemConverter<OrganizationFilterListItemAwareEntity, DirOrganizationListItemDto> organizationDirectoryDtoConverter;

    @Autowired
    private Converter<Pair<Marketplace, Long>, MarketplaceDto> marketplaceDtoConverter;

    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    private ItemConverter<MarketplaceDto, Marketplace> marketplaceConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private OrganizationSecurityService organizationSecurityService;

    @Autowired
    private Converter<OrganizationDto, List<AffiliatedOrganization>> affiliatedOrganizationEntityListConverter;

    @Autowired
    private Converter<List<AffiliatedOrganization>, List<AffiliatedRelationshipItemDto>> affiliatedRelationshipDtoListConverter;

    @Autowired
    private Converter<List<AffiliatedOrganization>, List<AffiliatedOrganizationDto>> affiliatedOrganizationDtoListConverter;

    @Autowired
    private AffiliatedOrganizationService affiliatedOrganizationService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    private AffiliatedRelationshipNotificationService affiliatedNotificationService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ValidationService validationService;

    @Override
    @PreAuthorize("@organizationSecurityService.canViewList()")
    @Transactional(readOnly = true)
    public List<DirOrganizationListItemDto> findAll(OrganizationFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var dtos = organizationDirectoryDtoConverter.convertList(organizationService.findForFilter(filter, permissionFilter));
        if (filter.isCheckCommunitiesExist()) {
            dtos.forEach(dto -> dto.setHasCommunities(communityService.hasVisibleCommunities(permissionFilter, dto.getId())));
        }
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@organizationSecurityService.canViewList()")
    public Page<OrganizationListItemDto> find(Pageable pageable, String name) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var organizationListItems = organizationService
                .findAll(name, permissionFilter, PaginationUtils.applyEntitySort(pageable, OrganizationListItemDto.class));
        return organizationListItems.map(organizationListItemDtoConverter::convert);
    }

    @Override
    @Transactional
    @PreAuthorize("@organizationSecurityService.canAdd()")
    public Long add(OrganizationDto organizationDto) {
        return save(organizationDto);
    }

    @Override
    @Transactional
    @PreAuthorize("@organizationSecurityService.canEdit(#organizationDto.id)")
    public Long edit(@P("organizationDto") OrganizationDto organizationDto) {
        return save(organizationDto);
    }

    private Long save(OrganizationDto organizationDto) {
        var organization = Objects.requireNonNull(organizationConverter.convert(organizationDto));

        var savedOrganization = organizationService.save(organization, false);

        saveAffiliatedOrganizations(organizationDto, savedOrganization.getId());

        saveMarketplaceForOrganization(organizationDto.getMarketplace(), savedOrganization.getId());

        saveLogo(organizationDto, savedOrganization);

        saveFeatures(organizationDto, savedOrganization);

        return savedOrganization.getId();
    }

    private void saveLogo(OrganizationDto organizationDto, Organization savedOrganization) {
        if (organizationDto.getLogo() != null || organizationDto.isShouldRemoveLogo()) {
            organizationService.uploadOrganizationLogo(savedOrganization.getId(), organizationDto.getLogo(),
                    organizationDto.isShouldRemoveLogo());
        }
    }

    private void saveFeatures(OrganizationDto organizationDto, Organization savedOrganization) {
        if (organizationSecurityService.canEditFeatures(savedOrganization.getId())) {
            validationService.validate(organizationDto, ValidationGroups.OrganizationFeatures.class);
            organizationService.updateOrganizationFeatures(savedOrganization.getId(), organizationDto.getFeatures());
        } else if (organizationDto.getId() == null) {
            organizationService.updateOrganizationFeatures(savedOrganization.getId(), OrganizationFeatures.DEFAULT);
        }
    }

    private void saveMarketplaceForOrganization(MarketplaceDto marketplaceDto, Long organizationId) {
        Marketplace marketplace;
        if (marketplaceDto.getId() != null) {
            // todo should we really search by organization id or we can use just
            // marketplaceId? Check logs and remove them once figured out.
            logger.info("Saving existing marketplace for organization. Given marketplaceId [{}], organizationId [{}]",
                    marketplaceDto.getId(), organizationId);
            marketplace = marketplaceService.findByOrgId(organizationId);
            logger.info("Actual marketplaceId [{}]", marketplace.getId());
            marketplaceConverter.convert(marketplaceDto, marketplace);
        } else {
            marketplace = Objects.requireNonNull(marketplaceConverter.convert(marketplaceDto));
            marketplace.setOrganizationId(organizationId);
            marketplace.setOrganization(organizationService.getOne(organizationId));
        }

        if (organizationSecurityService.canConfigureMarketplace(organizationId)) {
            marketplace.setDiscoverable(marketplaceDto.getConfirmVisibility());
        } else if (marketplace.getId() == null) {
            marketplace.setDiscoverable(MarketplaceService.DEFAULT_DISCOVERABLE);
        }

        marketplaceService.save(marketplace);
    }

    private void saveAffiliatedOrganizations(OrganizationDto organizationDto, Long organizationId) {

        if (organizationSecurityService.canConfigureAffiliateRelationships(organizationId)) {

            var affOrganizations = Stream.ofNullable(affiliatedOrganizationEntityListConverter.convert(organizationDto))
                    .flatMap(List::stream)
                    .peek(relationship -> relationship.setPrimaryOrganizationId(organizationId))
                    .collect(Collectors.toList());
            var existedAffOrganizations = affiliatedOrganizationService.getAllByPrimaryOrganizationId(organizationId);

            if (CollectionUtils.isEmpty(affOrganizations) && CollectionUtils.isEmpty(existedAffOrganizations)) {
                return;
            }

            var oldAffOrganizationDtos = affiliatedOrganizationDtoListConverter.convert(existedAffOrganizations);
            var newAffOrganizationDtos = affiliatedOrganizationDtoListConverter.convert(affiliatedOrganizationService.update(affOrganizations, organizationId));
            var created = createAffiliatedOrganizationDtoSet();
            var terminated = createAffiliatedOrganizationDtoSet();
            fillDelta(Objects.requireNonNull(oldAffOrganizationDtos), Objects.requireNonNull(newAffOrganizationDtos), created, terminated);

            var terminatedByAffOrgIds = terminated.stream()
                    .collect(Collectors.groupingBy(AffiliatedOrganizationDto::getAffiliatedOrganizationId));
            sendNotifications(created, terminated);
            deleteCareTeamMembers(terminatedByAffOrgIds);
        }
    }

    private Set<AffiliatedOrganizationDto> createAffiliatedOrganizationDtoSet() {
        return new TreeSet<>(new TreeSet<>(Comparator.comparingLong(AffiliatedOrganizationDto::getPrimaryCommunityId)
                .thenComparingLong(AffiliatedOrganizationDto::getAffiliatedCommunityId)));
    }

    private void fillDelta(List<AffiliatedOrganizationDto> oldAffOrganizationDtos, List<AffiliatedOrganizationDto> newAffOrganizationDtos, Set<AffiliatedOrganizationDto> created, Set<AffiliatedOrganizationDto> terminated) {
        var oldAffOrganizationDtoByAffOrgIds = oldAffOrganizationDtos.stream().collect(Collectors.groupingBy(AffiliatedOrganizationDto::getAffiliatedOrganizationId));
        var newAffOrganizationDtoByAffOrgIds = newAffOrganizationDtos.stream().collect(Collectors.groupingBy(AffiliatedOrganizationDto::getAffiliatedOrganizationId));

        newAffOrganizationDtoByAffOrgIds.forEach((affOrgId, newAffOrgDtos) -> {
            var oldAffOrgDtos = oldAffOrganizationDtoByAffOrgIds.remove(affOrgId);
            if (CollectionUtils.isEmpty(oldAffOrgDtos)) {
                created.addAll(newAffOrgDtos);
            } else {
                intersectionCommunities(oldAffOrgDtos, newAffOrgDtos, AffiliatedOrganizationDto::getAffiliatedCommunityId, created, terminated);
                intersectionCommunities(oldAffOrgDtos, newAffOrgDtos, AffiliatedOrganizationDto::getPrimaryCommunityId, created, terminated);
            }
        });
        if (MapUtils.isNotEmpty(oldAffOrganizationDtoByAffOrgIds)) {
            oldAffOrganizationDtoByAffOrgIds.values().forEach(terminated::addAll);
        }
    }

    private void intersectionCommunities(List<AffiliatedOrganizationDto> oldAffOrgDtos, List<AffiliatedOrganizationDto> newAffOrgDtos, Function<AffiliatedOrganizationDto, Long> keyMapper, Set<AffiliatedOrganizationDto> created, Set<AffiliatedOrganizationDto> terminated) {
        var oldAffOrgDtosByCommunityIds = oldAffOrgDtos.stream()
                .collect(Collectors.toMap(keyMapper, Function.identity(), (existing, replacement) -> existing, HashMap::new));
        var newAffOrgDtosByCommunityIds = newAffOrgDtos.stream()
                .collect(Collectors.toMap(keyMapper, Function.identity(), (existing, replacement) -> existing, HashMap::new));
        newAffOrgDtosByCommunityIds.forEach((communityId, newAffOrgDtosByCommunityId) -> {
            var oldAffOrgDtosByCommunityId = oldAffOrgDtosByCommunityIds.remove(communityId);
            if (oldAffOrgDtosByCommunityId == null) {
                created.add(newAffOrgDtosByCommunityId);
            }
        });
        if (MapUtils.isNotEmpty(oldAffOrgDtosByCommunityIds)) {
            terminated.addAll(oldAffOrgDtosByCommunityIds.values());
        }
    }

    private void sendNotifications(
        Collection<AffiliatedOrganizationDto> created,
        Collection<AffiliatedOrganizationDto> terminated
    ) {

        var organizationAdminsMap = new HashMap<Long, Set<Long>>();

        var notificationToRecipientsMap = new HashMap<AffiliatedNotificationDto, Set<Long>>();

        fillNotificationMap(organizationAdminsMap, notificationToRecipientsMap, created, false);
        fillNotificationMap(organizationAdminsMap, notificationToRecipientsMap, terminated, true);

        notificationToRecipientsMap.forEach((notification, recipients) ->
            affiliatedNotificationService.sentNotification(notification, recipients)
        );
    }

    private void fillNotificationMap(
        Map<Long, Set<Long>> organizationIdToAdminIdMap,
        Map<AffiliatedNotificationDto, Set<Long>> notificationToRecipientsMap,
        Collection<AffiliatedOrganizationDto> dtos,
        boolean isTerminated
    ) {
        dtos.forEach(dto -> {
            var notification = new AffiliatedNotificationDto(
                dto.getPrimaryOrganizationId(),
                dto.getAffiliatedOrganizationId(),
                isTerminated
            );

            var organizationAdmins = organizationIdToAdminIdMap.computeIfAbsent(
                dto.getAffiliatedOrganizationId(),
                (k) -> CareCoordinationUtils.toIdsSet(employeeService.findInOrganizationWithRole(
                    dto.getAffiliatedOrganizationId(),
                    CareTeamRoleCode.ROLE_ADMINISTRATOR
                ))
            );

            var communityAdmins = CareCoordinationUtils.toIdsSet(employeeService.findInCommunityWithRole(
                dto.getAffiliatedCommunityId(),
                CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR
            ));

            var recipients = notificationToRecipientsMap.computeIfAbsent(notification, (k) -> new HashSet<>());

            recipients.addAll(organizationAdmins);
            recipients.addAll(communityAdmins);
        });
    }

    private void deleteCareTeamMembers(Map<Long, List<AffiliatedOrganizationDto>> dtosByAffOrgIds) {
        var allConsent = HieConsentCareTeamType.currentAndOnHold();
        for (var dtosByAffOrgId : dtosByAffOrgIds.entrySet()) {
            var dtos = dtosByAffOrgId.getValue();
            var primaryCommunityIds = dtos.stream()
                    .map(AffiliatedOrganizationDto::getPrimaryCommunityId)
                    .collect(Collectors.toSet());
            var clientCareTeamMemberIds = clientCareTeamMemberService.findClientCareTeamMemberIds(
                    primaryCommunityIds,
                    dtosByAffOrgId.getKey(),
                    allConsent
            );
            clientCareTeamMemberService.deleteByIds(clientCareTeamMemberIds);
            var communitiesCareTeamMemberIds = communityCareTeamMemberService.findCommunityCareTeamMemberIds(
                    primaryCommunityIds,
                    dtosByAffOrgId.getKey(),
                    allConsent
            );
            communityCareTeamMemberService.deleteByIds(communitiesCareTeamMemberIds);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@organizationSecurityService.canView(#id)")
    public OrganizationBaseDto findById(Long id, Boolean marketplaceDataIncluded) {
        var organization = organizationService.findById(id);
        var affiliatedOrganizations = affiliatedOrganizationService.getAllByPrimaryOrganizationId(id);
        OrganizationDto organizationDto = null;
        if (CollectionUtils.isNotEmpty(affiliatedOrganizations)) {
            organizationDto = organizationDtoConverter.convert(organization);
            Objects.requireNonNull(organizationDto).setAffiliatedRelationships(affiliatedRelationshipDtoListConverter.convert(affiliatedOrganizations));
        }
        if (BooleanUtils.isNotFalse(marketplaceDataIncluded)) {
            var marketplace = marketplaceService.findByOrgId(id);
            organizationDto = organizationDto == null ? organizationDtoConverter.convert(organization) : organizationDto;
            if (marketplace != null) {
                Objects.requireNonNull(organizationDto).setMarketplace(marketplaceDtoConverter.convert(Pair.of(marketplace, null)));
            }
            return organizationDto;
        } else {
            return organizationBasicDtoItemConverter.convert(organization);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@organizationSecurityService.canViewList()")
    public Long count() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return organizationService.count(permissionFilter);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd() {
        return organizationSecurityService.canAdd();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@organizationSecurityService.canViewLogo(#id)")
    public FileBytesDto downloadLogo(Long id) {
        Optional<Pair<byte[], MediaType>> bytesWithMediaType = Optional.ofNullable(organizationService.downloadLogo(id));
        return bytesWithMediaType.map(mediaTypePair -> new FileBytesDto(mediaTypePair.getFirst(), mediaTypePair.getSecond())).orElse(new FileBytesDto());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#organizationId == null ? " +
            "@organizationSecurityService.canAdd() : " +
            "@organizationSecurityService.canEdit(#organizationId)")
    public OrganizationUniquenessDto validateUniqueFields(String oid, String name, String companyId, @P("organizationId") Long organizationId) {
        Boolean oidValid = oid != null ? !organizationService.existsByOid(oid) : null;
        Boolean nameValid = name != null ? !organizationService.existsByName(name) : null;
        Boolean companyIdValid = companyId != null ? !organizationService.existsByCompanyId(companyId) : null;
        return new OrganizationUniquenessDto(oidValid, nameValid, companyIdValid);
    }

    @Override
    @PreAuthorize("@organizationSecurityService.canViewList()")
    @Transactional(readOnly = true)
    public List<IdentifiedTitledEntityDto> findChatAccessible(ConversationParticipantAccessibilityFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        return organizationService.findChatAccessible(permissionFilter, filter).stream()
                .map(community -> new IdentifiedTitledEntityDto(community.getId(), community.getName())).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationPermissionsDto getPermissions() {
        var permissions = new OrganizationPermissionsDto();
        permissions.setCanAdd(organizationSecurityService.canAdd());
        permissions.setCanEditFeatures(organizationSecurityService.canEditFeatures(null));
        var canConfigureMarketplace = organizationSecurityService.canConfigureMarketplace(null);
        permissions.setCanEditConfirmMarketplaceVisibility(canConfigureMarketplace);
        permissions.setCanEditAllowExternalInboundReferrals(canConfigureMarketplace);
        permissions.setCanEditAffiliateRelationships(organizationSecurityService.canConfigureAffiliateRelationships(null));
        return permissions;
    }
}
