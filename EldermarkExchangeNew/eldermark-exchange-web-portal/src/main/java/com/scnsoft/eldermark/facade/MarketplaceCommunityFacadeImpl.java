package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.FeaturedServiceProviderFilter;
import com.scnsoft.eldermark.beans.MarketplaceFilter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dao.ServicesTreatmentApproachDao;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.DirectAccountDetails;
import com.scnsoft.eldermark.service.DirectAccountDetailsFactory;
import com.scnsoft.eldermark.service.MarketplaceService;
import com.scnsoft.eldermark.service.SavedMarketplaceService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.MarketplaceCommunitySecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarketplaceCommunityFacadeImpl implements MarketplaceCommunityFacade {

    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    private ListAndItemConverter<Marketplace, MarketplaceCommunitySummaryDto> marketplaceCommunitySummaryDtoConverter;

    @Autowired
    private ServicesTreatmentApproachDao servicesTreatmentApproachDao;

    @Autowired
    private Converter<Community, CommunityWithAddressDetailsDto> communityDtoConverter;

    @Autowired
    private Converter<Pair<Marketplace, Long>, MarketplaceDto> marketplaceDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private SavedMarketplaceService savedMarketplaceService;

    @Autowired
    private Converter<Marketplace, MarketplaceCommunityLocationDetailsDto> marketplaceCommunityLocationDetailsDtoConverter;

    @Autowired
    private Converter<Marketplace, MarketplaceSavedCommunitySummaryDto> marketplaceSavedCommunitySummaryDtoConverter;

    @Autowired
    private Converter<Marketplace, MarketplaceCommunityLocationListItemDto> marketplaceCommunityLocationListItemDtoConverter;

    @Autowired
    private FeaturedServiceProviderFacade featuredServiceProviderFacade;

    @Autowired
    private Converter<Marketplace, FeaturedServiceProviderDto> marketplaceFeaturedServiceProviderDtoConverter;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewPartnerProviders(#communityId)")
    public Page<MarketplaceCommunitySummaryDto> findPartners(Long communityId, MarketplaceFilter filter,
                                                             Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var marketplace = marketplaceService.findByCommunityId(communityId);
        var community = marketplace.getCommunity();
        if (community == null) {
            return PaginationUtils.buildEmptyPage();
        }

        pageable = PaginationUtils.defaultPage(pageable);

        var partners = marketplaceService.findPartners(marketplace, filter, permissionFilter, pageable);

        partners.forEach(x -> {
            x.setUserLatitude(filter.getLatitude());
            x.setUserLongitude(filter.getLongitude());
        });

        return partners.map(marketplaceCommunitySummaryDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewList()")
    public Page<MarketplaceCommunitySummaryDto> find(MarketplaceFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        pageable = PaginationUtils.defaultPage(pageable);
        var marketplaces = marketplaceService.find(filter, permissionFilter, pageable);

        if (CollectionUtils.isNotEmpty(marketplaces.getContent()))
            marketplaces.getContent().forEach(x -> {
                x.setUserLatitude(filter.getLatitude());
                x.setUserLongitude(filter.getLongitude());
            });
        return marketplaces.map(marketplaceCommunitySummaryDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewByCommunityId(#communityId)")
    public CommunityWithAddressDetailsDto findByCommunityId(@P("communityId") Long communityId, Long referralClientId) {
        var marketplace = marketplaceService.findByCommunityId(communityId);
        var communityDto = Objects.requireNonNull(communityDtoConverter.convert(marketplace.getCommunity()));

        communityDto.setMarketplace(marketplaceDtoConverter.convert(Pair.of(marketplace, referralClientId)));
        communityDto.setCanViewPartners(
                !Collections.isEmpty(marketplace.getMarketplacePartnerNetworks())
                        && marketplaceCommunitySecurityService.canViewPartnerProviders(communityId)
        );

        return communityDto;
    }

    @Deprecated
    private AppointmentMailDto createAppointMailDto(Marketplace marketplace,
                                                    MarketplaceCommunityAppointmentDto appointmentDto) {
        List<ServicesTreatmentApproach> servicesList = servicesTreatmentApproachDao.findAllById(appointmentDto.getServiceIds());

        AppointmentMailDto mailDto = new AppointmentMailDto();

        //mailDto.setToEmail(marketplace.getSecureEmail());
        mailDto.setOrganizationName(marketplace.getOrganization().getName());
        mailDto.setCommunityName(marketplace.getCommunity().getName());
        mailDto.setName(appointmentDto.getName());
        mailDto.setPhone(appointmentDto.getPhone());
        mailDto.setEmail(appointmentDto.getEmail());
        mailDto.setServices(servicesList.stream().map(DisplayableNamedEntity::getDisplayName).collect(Collectors.toList()));
        mailDto.setAppointmentDate(appointmentDto.getAppointmentDate());
        mailDto.setIsEmergencyVisit(BooleanUtils.toString(appointmentDto.getIsUrgentCare(), "Yes", "No", "No"));
        mailDto.setComment(appointmentDto.getComment());
        mailDto.setDaysToContact(Optional.ofNullable(marketplace.getOrganization().getMaxDaysToProcessAppointment()).orElse(3));

        return mailDto;

    }

    @Override
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewList()")
    public Page<MarketplaceSavedCommunitySummaryDto> findSavedMarketplaces(Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var employeeId = loggedUserService.getCurrentEmployeeId();
        pageable = PaginationUtils.setSort(pageable, Sort.by(CareCoordinationUtils.concat(".", Client_.ORGANIZATION, Organization_.NAME),
                CareCoordinationUtils.concat(".", Client_.COMMUNITY, Community_.NAME)));
        var marketplaces = marketplaceService.findSaved(permissionFilter, employeeId, pageable);
        return marketplaces.map(marketplaceSavedCommunitySummaryDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewByCommunityId(#communityId)")
    public void addSavedMarketplaceByCommunityId(Long communityId) {
        var employeeId = loggedUserService.getCurrentEmployeeId();
        var marketplaceId = marketplaceService.findIdAwareByCommunityId(communityId).getId();
        savedMarketplaceService.save(employeeId, marketplaceId);
    }

    @Override
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewByCommunityId(#communityId)")
    public void removeSavedMarketplaceByCommunityId(Long communityId) {
        var employeeId = loggedUserService.getCurrentEmployeeId();
        var marketplaceId = marketplaceService.findIdAwareByCommunityId(communityId).getId();
        savedMarketplaceService.remove(employeeId, marketplaceId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewList()")
    public List<MarketplaceCommunityLocationListItemDto> findLocations(MarketplaceFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var pageable = PaginationUtils.createLargestPage();
        var marketplaces = marketplaceService.find(filter, permissionFilter, pageable);

        if (CollectionUtils.isNotEmpty(marketplaces.getContent()))
            marketplaces.getContent().forEach(x -> {
                x.setUserLatitude(filter.getLatitude());
                x.setUserLongitude(filter.getLongitude());
            });
        return CollectionUtils.emptyIfNull(marketplaces.getContent()).stream().map(marketplaceCommunityLocationListItemDtoConverter::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewByCommunityId(#communityId)")
    public MarketplaceCommunityLocationDetailsDto findLocationDetails(Long communityId) {
        var marketplace = marketplaceService.findByCommunityId(communityId);
        return marketplaceCommunityLocationDetailsDtoConverter.convert(marketplace);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewFeaturedPartnerProviders(#filter.communityId)")
    public Page<FeaturedServiceProviderDto> fetchFeaturedServiceProviders(FeaturedServiceProviderFilter filter, Pageable pageable) {
        var marketplace = marketplaceService.findByCommunityId(filter.getCommunityId());

        if (marketplace == null) {
            return PaginationUtils.buildEmptyPage();
        }

        var featuredServiceProviderDtos = featuredServiceProviderFacade.fetchServiceProviders(filter);

        if (BooleanUtils.isTrue(filter.isFeatured())) {
            return new PageImpl<>(featuredServiceProviderDtos);
        }

        var marketplaceServiceProviderDtos = marketplaceService.fetchServiceProviders(
                        marketplace,
                        PaginationUtils.defaultPage(pageable)
                )
                .map(marketplaceFeaturedServiceProviderDtoConverter::convert)
                .getContent()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var idsToExclude = featuredServiceProviderDtos.stream()
                .map(FeaturedServiceProviderDto::getCommunityId)
                .collect(Collectors.toList());

        marketplaceServiceProviderDtos = marketplaceServiceProviderDtos.stream()
                .filter(dto -> !idsToExclude.contains(dto.getCommunityId()))
                .collect(Collectors.toList());

        marketplaceServiceProviderDtos.addAll(featuredServiceProviderDtos);
        return new PageImpl<>(marketplaceServiceProviderDtos);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@marketplaceCommunitySecurityService.canViewList()")
    public boolean existsInNetworkMarketplaceAccessibleCommunities() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return marketplaceService.existsInNetworkMarketplaceAccessibleCommunities(permissionFilter);
    }
}
