package com.scnsoft.eldermark.services.marketplace;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.maps.model.LatLng;
import com.scnsoft.eldermark.dao.marketplace.AgeGroupDao;
import com.scnsoft.eldermark.dao.marketplace.AncillaryServiceDao;
import com.scnsoft.eldermark.dao.marketplace.CommunityTypeDao;
import com.scnsoft.eldermark.dao.marketplace.EmergencyServiceDao;
import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.dao.marketplace.LanguageServiceDao;
import com.scnsoft.eldermark.dao.marketplace.LevelOfCareDao;
import com.scnsoft.eldermark.dao.marketplace.MarketplaceCustomDao;
import com.scnsoft.eldermark.dao.marketplace.MarketplaceDao;
import com.scnsoft.eldermark.dao.marketplace.PrimaryFocusDao;
import com.scnsoft.eldermark.dao.marketplace.ServicesTreatmentApproachDao;
import com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao;
import com.scnsoft.eldermark.entity.Address;
import com.scnsoft.eldermark.entity.OrganizationAddress;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.marketplace.AgeGroup;
import com.scnsoft.eldermark.entity.marketplace.AncillaryService;
import com.scnsoft.eldermark.entity.marketplace.CommunityType;
import com.scnsoft.eldermark.entity.marketplace.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.marketplace.EmergencyService;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.marketplace.LanguageService;
import com.scnsoft.eldermark.entity.marketplace.LevelOfCare;
import com.scnsoft.eldermark.entity.marketplace.Marketplace;
import com.scnsoft.eldermark.entity.marketplace.PrimaryFocus;
import com.scnsoft.eldermark.entity.marketplace.ServicesTreatmentApproach;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.OrganizationAddressService;
import com.scnsoft.eldermark.services.marketplace.internal.AgeGroupsDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.AncillaryServicesDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.CommunityTypesDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.EmergencyServicesDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.EntityListUtils;
import com.scnsoft.eldermark.services.marketplace.internal.InNetworkInsurancesDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.InNetworkInsurancesExceptSection0DtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.InNetworkInsurancesGroupNoNameDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.InNetworkInsurancesPopularDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.InsurancePlansDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.LanguageServiceDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.LevelsOfCareDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.PopularInsurancePlansDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.PrimaryFocusesDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.ServicesTreatmentApproachDtoSupplier;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyTwoValuesDto;
import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyTwoValuesDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryFocusKeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.marketplace.BasicMarketplaceInfoDto;
import com.scnsoft.eldermark.shared.marketplace.LocationDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDetailsDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceInfoDto;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.*;

/**
 * @author phomal
 * Created by phomal on 11/27/2017.
 */
@Service
public class MarketplaceServiceImpl implements MarketplaceService {

    @Autowired
    private MarketplaceDao marketplaceDao;
    @Autowired
    private MarketplaceCustomDao marketplaceCustomDao;
    @Autowired
    private AgeGroupDao ageGroupDao;
    @Autowired
    private AncillaryServiceDao ancillaryServiceDao;
    @Autowired
    private CommunityTypeDao communityTypeDao;
    @Autowired
    private EmergencyServiceDao emergencyServiceDao;
    @Autowired
    private InNetworkInsuranceDao inNetworkInsuranceDao;
    @Autowired
    private InsurancePlanDao insurancePlanDao;
    @Autowired
    private LanguageServiceDao languageServiceDao;
    @Autowired
    private LevelOfCareDao levelOfCareDao;
    @Autowired
    private PrimaryFocusDao primaryFocusDao;
    @Autowired
    private ServicesTreatmentApproachDao servicesTreatmentApproachDao;

    @Autowired
    private PrimaryFocusesDtoSupplier primaryFocusesDtoSupplier;
    @Autowired
    private AncillaryServicesDtoSupplier ancillaryServicesDtoSupplier;
    @Autowired
    private AgeGroupsDtoSupplier ageGroupsDtoSupplier;
    @Autowired
    private CommunityTypesDtoSupplier communityTypeDtoSupplier;
    @Autowired
    private EmergencyServicesDtoSupplier emergencyServicesDtoSupplier;
    @Autowired
    private LanguageServiceDtoSupplier languageServiceDtoSupplier;
    @Autowired
    private LevelsOfCareDtoSupplier levelsOfCareDtoSupplier;
    @Autowired
    private ServicesTreatmentApproachDtoSupplier servicesTreatmentApproachDtoSupplier;
    @Autowired
    private InNetworkInsurancesDtoSupplier inNetworkInsurancesDtoSupplier;
//    @Autowired
//    private InNetworkInsurancesPopularDtoSupplier inNetworkInsurancesPopularDtoSupplier;
    @Autowired
    private InsurancePlansDtoSupplier insurancePlansDtoSupplier;
    @Autowired
    private InNetworkInsurancesGroupNoNameDtoSupplier inNetworkInsurancesGroupNoNameDtoSupplier;
    @Autowired
    private InNetworkInsurancesExceptSection0DtoSupplier inNetworkInsurancesExceptSection0DtoSupplier;
    @Autowired
    private InNetworkInsurancesPopularDtoSupplier inNetworkInsurancesPopularDtoSupplier;
    @Autowired
    private PopularInsurancePlansDtoSupplier popularInsurancePlansDtoSupplier;

    @Autowired
    private Converter<Marketplace, Map<String, String[]>> selectedInNetworkInsurancePlansConverter;

    @Autowired
    private MapsService mapsService;
    @Autowired
    StateService stateService;

    @Autowired
    private OrganizationAddressService organizationAddressService;

    @Override
    public List<KeyValueDto> getPrimaryFocuses() {
        return primaryFocusesDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getCommunityTypes() {
        return communityTypeDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getLevelsOfCare() {
        return levelsOfCareDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getAgeGroups() {
        return ageGroupsDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getServicesTreatmentApproaches() {
        return servicesTreatmentApproachDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getEmergencyServices() {
        return emergencyServicesDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getLanguageServices() {
        return languageServiceDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getAncillaryServices() {
        return ancillaryServicesDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getInNetworkInsurances() {
        return inNetworkInsurancesDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getNetworkInsurancesGroupWithoutName() {
        return inNetworkInsurancesGroupNoNameDtoSupplier.getMemoized();
    }

    @Override
    public List<KeyValueDto> getPopularInNetworkInsurances() {
        return inNetworkInsurancesPopularDtoSupplier.getMemoized();
    }

    @Override
    public List<AlphabetableKeyValueDto> getInNetworkInsurancesExceptSection0() {
        return inNetworkInsurancesExceptSection0DtoSupplier.getMemoized();
    }

    @Override
    public Map<Long, List<AlphabetableKeyTwoValuesDto>> getInsurancePlans() {
        return insurancePlansDtoSupplier.getMemoized();
    }

    @Override
    public Map<Long, Collection<KeyTwoValuesDto>> getPopularInsurancePlans() {
        return popularInsurancePlansDtoSupplier.getMemoized();
    }

    @Override
    public MarketplaceDto getMarketplaceByOrganizationId(Long orgId) {
        return convert(marketplaceDao.getOneByDatabaseIdAndOrganizationIsNull(orgId));
    }

    @Override
    public MarketplaceDto getMarketplaceByCommunityId(Long commId) {
        return convert(marketplaceDao.getOneByOrganizationId(commId));
    }

    @Override
    public MarketplaceDto updateForOrganization(Long orgId, MarketplaceDto marketplaceDto) {
        Marketplace marketplace = marketplaceDao.getOneByDatabaseIdAndOrganizationIsNull(orgId);
        marketplace = convert(marketplaceDto, marketplace);
        marketplace.setDatabaseId(orgId);

        return convert(marketplaceDao.save(marketplace));
    }

    @Override
    public MarketplaceDto updateForCommunity(Long orgId, Long commId, MarketplaceDto marketplaceDto) {
        Marketplace marketplace = marketplaceDao.getOneByOrganizationId(commId);
        marketplace = convert(marketplaceDto, marketplace);
        marketplace.setDatabaseId(orgId);
        marketplace.setOrganizationId(commId);

        return convert(marketplaceDao.save(marketplace));
    }

    @Override
    public void deleteForOrganization(Long orgId) {
        marketplaceDao.deleteOneByDatabaseIdAndOrganizationIsNull(orgId);
    }

    @Override
    public void deleteForCommunity(Long communityId) {
        if (communityId != null) {
            marketplaceDao.deleteOneByOrganizationId(communityId);
        }
    }

    @Override
    public List<KeyValueDto> searchInNetworkInsurance(String query) {
        // TODO implement
        return Arrays.asList(new KeyValueDto(1L, "asdasd"), new KeyValueDto(2L, "222222"));
    }

//    @Override
//    public List<KeyValueDto> getPopularInNetworkInsurances() {
//        return inNetworkInsurancesPopularDtoSupplier.getMemoized();
//    }

    public Page<BasicMarketplaceInfoDto> getDiscoverableMarketplaces(List<Long> careTypeIds, List<Long> communityTypeIds, List<Long> servicesTreatmentApproachesIds,
                                                                     List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices,
                                                                     String location, String locationType, String searchText, Double userLatitude, Double userLongitude, Pageable pageable) {
        AddressDto address = null;
        if (location != null) {
            address = parseLocation(location, locationType);
        }
        getOrganizationAddressService().populateAllLocationForOutdatedAddresses();
        Page<Marketplace> marketplaces = marketplaceCustomDao.filterMarketplaces(careTypeIds, communityTypeIds, null, servicesTreatmentApproachesIds, inNetworkInsurancesIds, insurancePlansIds, emergencyServices, address, searchText, pageable, userLatitude, userLongitude);
        return convert(marketplaces, userLatitude, userLongitude, pageable);
    }

    public List<MarketplaceInfoDto> getMarketplacesByIds(List<Long>ids) {

        List<Marketplace> marketplaces = marketplaceCustomDao.getMarketpaces(ids);
        List<MarketplaceInfoDto> marketplaceListInfoDtoList = new ArrayList();
        for (Marketplace marketplace: marketplaces) {
            MarketplaceInfoDto marketplaceListInfoDto = new MarketplaceInfoDto();
            addBasicInfo(marketplace, null, null, marketplaceListInfoDto);
            marketplaceListInfoDto.setPhoneNumber(marketplace.getOrganization().getPhone());
            marketplaceListInfoDtoList.add(marketplaceListInfoDto);
        }
        return marketplaceListInfoDtoList;
    }

    private Page<BasicMarketplaceInfoDto> convert(Page<Marketplace> marketplaces, Double userLatitude, Double userLongitude, Pageable pageable) {
        List<Marketplace> marketplaceList = marketplaces.getContent();
        List<BasicMarketplaceInfoDto> marketplaceListInfoDtoList = new ArrayList();
        if (!org.springframework.util.CollectionUtils.isEmpty(marketplaceList)) {
            for (Marketplace marketplace : marketplaceList) {
                marketplaceListInfoDtoList.add(transformListItem(marketplace, userLatitude, userLongitude));
            }
        }
        return new PageImpl(marketplaceListInfoDtoList, pageable, marketplaces.getTotalElements());
    }

    public MarketplaceDetailsDto getMarketplaceDetails(Long id) {
        Marketplace marketplace = marketplaceDao.getOne(id);
        MarketplaceDetailsDto marketplaceDetailsDto = new MarketplaceDetailsDto();
        addBasicInfo(marketplace, null, null, marketplaceDetailsDto);
        marketplaceDetailsDto.setLevelOfCares(convert(marketplace.getLevelsOfCare()));
        marketplaceDetailsDto.setServiceTreatmentApproaches(convert(marketplace.getServicesTreatmentApproaches()));
        marketplaceDetailsDto.setEmergencyServices(convert(marketplace.getEmergencyServices()));
        marketplaceDetailsDto.setAgeGroupsAccepted(convert(marketplace.getAcceptedAgeGroups()));
        marketplaceDetailsDto.setLanguageServices(convert(marketplace.getLanguageServices()));
        marketplaceDetailsDto.setAncillaryServices(convert(marketplace.getAncillaryServices()));
        marketplaceDetailsDto.setServicesSummaryDescription(marketplace.getSummary());
        marketplaceDetailsDto.setPhoneNumber(marketplace.getOrganization().getPhone());
        marketplaceDetailsDto.setSelectedInNetworkInsurancePlanNames(getSelectedInNetworkInsurancePlansConverter().convert(marketplace));

        return marketplaceDetailsDto;
    }

    private List<String> convert(Set<? extends DisplayableNamedEntity> list) {
        List<String>result = new ArrayList();
        for (DisplayableNamedEntity entity: list) {
            result.add(entity.getDisplayName());
        }
        Collections.sort(result);
        return result;
    }

    private BasicMarketplaceInfoDto transformListItem(Marketplace marketplace, Double userLatitude, Double userLongitude) {
        BasicMarketplaceInfoDto marketplaceListInfoDto = new BasicMarketplaceInfoDto();
        addBasicInfo(marketplace, userLatitude, userLongitude, marketplaceListInfoDto);
        return marketplaceListInfoDto;
    }

    private void addBasicInfo(Marketplace marketplace, Double userLatitude, Double userLongitude, BasicMarketplaceInfoDto marketplaceListInfoDtoToAdd) {
        marketplaceListInfoDtoToAdd.setId(marketplace.getId());
        marketplaceListInfoDtoToAdd.setOrganizationName(marketplace.getDatabase().getName());
        if (marketplace.getOrganization() != null) {
            marketplaceListInfoDtoToAdd.setCommunityName(marketplace.getOrganization().getName());
            if (!org.springframework.util.CollectionUtils.isEmpty(marketplace.getOrganization().getAddresses())) {
                final OrganizationAddress organizationAddress = marketplace.getOrganization().getAddresses().get(0);
                final AddressDto addressDto = convertAddress(organizationAddress);
                marketplaceListInfoDtoToAdd.setAddress(addressDto.getDisplayAddress());
                final LocationDto locationDto = getLocationDto(userLongitude, userLatitude,
                        organizationAddress.getLongitude(), organizationAddress.getLatitude());
                marketplaceListInfoDtoToAdd.setLocation(locationDto);
            }
//            marketplaceListInfoDtoToAdd.setPhoneNumber(marketplace.getOrganization().getPhone());
        }
        List<String> communityTypes = new ArrayList();
        if (!org.springframework.util.CollectionUtils.isEmpty(marketplace.getCommunityTypes())) {
            for (CommunityType communityType : marketplace.getCommunityTypes()) {
                communityTypes.add(communityType.getDisplayName());
            }
        }
        marketplaceListInfoDtoToAdd.setCommunityTypes(communityTypes);
    }

    private LocationDto getLocationDto(Double userLongitude, Double userLatitude, Double longitude, Double latitude) {
        final LocationDto locationDto = new LocationDto();
        locationDto.setLatitude(latitude);
        locationDto.setLongitude(longitude);
        if (userLongitude == null || userLatitude == null || longitude == null || latitude == null) {
            return locationDto;
        }
        final LatLng userLocation = new LatLng(userLatitude, userLongitude );
        final LatLng pointLocation = new LatLng(latitude, longitude );
        final Double distanceInMiles = mapsService.calculateDistanceMiles(pointLocation, userLocation);
        locationDto.setDistanceInMiles(distanceInMiles);
        return locationDto;
    }

    private AddressDto convertAddress(Address organizationAddress) {
        final AddressDto addressDto =                 new AddressDto();
        if (!StringUtils.isEmpty(organizationAddress.getState())) {
            addressDto.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(organizationAddress.getState())));
        }
        addressDto.setCity(organizationAddress.getCity());
        addressDto.setStreet(organizationAddress.getStreetAddress());
        addressDto.setZip(organizationAddress.getPostalCode());
        return addressDto;
    }

    private static MarketplaceDto convert(Marketplace source) {
        final MarketplaceDto dto = new MarketplaceDto();
        if (source == null) {
            return dto;
        }

        dto.setConfirmVisibility(source.getDiscoverable());
        dto.setServicesSummaryDescription(source.getSummary());
        dto.setAllowAppointments(source.getAllowAppointments());
        dto.setAllInsurancesAccepted(source.getAllInsurancesAccepted());
        dto.setAppointmentsEmail(source.getEmail());
        dto.setAppointmentsSecureEmail(source.getSecureEmail());

        List<Long> ageGroupIds = new ArrayList<Long>();
        CollectionUtils.collect(source.getAcceptedAgeGroups(), new BeanToPropertyValueTransformer("id"), ageGroupIds);
        dto.setAgeGroupIds(ageGroupIds);

        List<Long> ancillaryServices = new ArrayList<Long>();
        CollectionUtils.collect(source.getAncillaryServices(), new BeanToPropertyValueTransformer("id"), ancillaryServices);
        dto.setAncillaryServiceIds(ancillaryServices);

        List<Long> communityTypeIds = new ArrayList<Long>();
        CollectionUtils.collect(source.getCommunityTypes(), new BeanToPropertyValueTransformer("id"), communityTypeIds);
        dto.setCommunityTypeIds(communityTypeIds);

        List<Long> emergencyServiceIds = new ArrayList<Long>();
        CollectionUtils.collect(source.getEmergencyServices(), new BeanToPropertyValueTransformer("id"), emergencyServiceIds);
        dto.setEmergencyServiceIds(emergencyServiceIds);

        List<Long> languageServiceIds = new ArrayList<Long>();
        CollectionUtils.collect(source.getLanguageServices(), new BeanToPropertyValueTransformer("id"), languageServiceIds);
        dto.setLanguageServiceIds(languageServiceIds);

        List<Long> levelOfCareIds = new ArrayList<Long>();
        CollectionUtils.collect(source.getLevelsOfCare(), new BeanToPropertyValueTransformer("id"), levelOfCareIds);
        dto.setLevelOfCareIds(levelOfCareIds);

        List<Long> primaryFocusIds = new ArrayList<Long>();
        CollectionUtils.collect(source.getPrimaryFocuses(), new BeanToPropertyValueTransformer("id"), primaryFocusIds);
        dto.setPrimaryFocusIds(primaryFocusIds);

        List<Long> servicesTreatmentApproachIds = new ArrayList<Long>();
        CollectionUtils.collect(source.getServicesTreatmentApproaches(), new BeanToPropertyValueTransformer("id"), servicesTreatmentApproachIds);
        dto.setServiceTreatmentApproachIds(servicesTreatmentApproachIds);

        Map<Long, List<Long>> selectedInNetworkInsurancePlanIds = new HashMap<>();
        if (CollectionUtils.isNotEmpty(source.getInNetworkInsurances())) {
            for (InNetworkInsurance inNetworkInsurance : source.getInNetworkInsurances()) {
                if (!selectedInNetworkInsurancePlanIds.containsKey(inNetworkInsurance.getId())) {
                    selectedInNetworkInsurancePlanIds.put(inNetworkInsurance.getId(), new ArrayList<Long>());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(source.getInsurancePlans())) {
            for (InsurancePlan insurancePlan : source.getInsurancePlans()) {
                if(!selectedInNetworkInsurancePlanIds.containsKey(insurancePlan.getInNetworkInsuranceId())) {
                    selectedInNetworkInsurancePlanIds.put(insurancePlan.getInNetworkInsuranceId(), new ArrayList<Long>());
                }
                selectedInNetworkInsurancePlanIds.get(insurancePlan.getInNetworkInsuranceId()).add(insurancePlan.getId());
            }
        }
        dto.setSelectedInNetworkInsurancePlanIds(selectedInNetworkInsurancePlanIds);
        dto.setSelectedInNetworkInsuranceIds(new ArrayList<>(selectedInNetworkInsurancePlanIds.keySet()));

        dto.setPrerequisite(source.getPrerequisite());
        dto.setExclusion(source.getExclusion());

        return dto;
    }

    private Marketplace convert(MarketplaceDto marketplaceDto, Marketplace marketplace) {
        if (marketplace == null) {
            marketplace = new Marketplace();
        }
        marketplace.setDiscoverable(Boolean.TRUE.equals(marketplaceDto.getConfirmVisibility()));
        marketplace.setSummary(marketplaceDto.getServicesSummaryDescription());
        marketplace.setAllowAppointments(Boolean.TRUE.equals(marketplaceDto.getAllowAppointments()));
        marketplace.setAllInsurancesAccepted(Boolean.TRUE.equals(marketplaceDto.getAllInsurancesAccepted()));
        marketplace.setEmail(StringUtils.trimToNull(marketplaceDto.getAppointmentsEmail()));
        marketplace.setSecureEmail(StringUtils.trimToNull(marketplaceDto.getAppointmentsSecureEmail()));

        marketplace.setAcceptedAgeGroups(new HashSet<AgeGroup>(ageGroupDao.findAll(marketplaceDto.getAgeGroupIds())));
        marketplace.setAncillaryServices(new HashSet<AncillaryService>(ancillaryServiceDao.findAll(marketplaceDto.getAncillaryServiceIds())));
        marketplace.setCommunityTypes(new HashSet<CommunityType>(communityTypeDao.findAll(marketplaceDto.getCommunityTypeIds())));
        marketplace.setEmergencyServices(new HashSet<EmergencyService>(emergencyServiceDao.findAll(marketplaceDto.getEmergencyServiceIds())));
        marketplace.setLanguageServices(new HashSet<LanguageService>(languageServiceDao.findAll(marketplaceDto.getLanguageServiceIds())));
        marketplace.setLevelsOfCare(new HashSet<LevelOfCare>(levelOfCareDao.findAll(marketplaceDto.getLevelOfCareIds())));
        marketplace.setPrimaryFocuses(new HashSet<PrimaryFocus>(primaryFocusDao.findAll(marketplaceDto.getPrimaryFocusIds())));
        marketplace.setServicesTreatmentApproaches(new HashSet<ServicesTreatmentApproach>(
                servicesTreatmentApproachDao.findAll(marketplaceDto.getServiceTreatmentApproachIds())));

        Set<Long> selectedNetworks = new HashSet<>();
        Set<Long> selectedPlans = new HashSet<>();
        if (MapUtils.isNotEmpty(marketplaceDto.getSelectedInNetworkInsurancePlanIds())) {
            for (Map.Entry<Long, List<Long>> entry: marketplaceDto.getSelectedInNetworkInsurancePlanIds().entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue())) {
                    selectedNetworks.add(entry.getKey());
                } else {
                   selectedPlans.addAll(entry.getValue());
                }
            }
        }
        marketplace.setInNetworkInsurances(new HashSet<>(inNetworkInsuranceDao.findAll(selectedNetworks)));
        marketplace.setInsurancePlans(new HashSet<>(insurancePlanDao.findAll(selectedPlans)));
        marketplace.setPrerequisite(StringUtils.trimToNull(marketplaceDto.getPrerequisite()));
        marketplace.setExclusion(StringUtils.trimToNull(marketplaceDto.getExclusion()));

        return marketplace;
    }

    private AddressDto parseLocation(String location, String locationType) {
        //TODO validate input
        String[] addressParts =location.split(",");
        AddressDto result = new AddressDto();
        if ("STATE".equalsIgnoreCase(locationType)) {
            State state = stateService.findByAbbrOrFullName(addressParts[0].trim());
            result.setState(new KeyValueDto(state.getId(), state.getAbbr()));
        } else if ("CITY".equalsIgnoreCase(locationType)) {
            result.setCity(addressParts[0].trim());
            State state = stateService.findByAbbrOrFullName(addressParts[1].trim());
            result.setState(new KeyValueDto(state.getId(), state.getAbbr()));
        } else if ("ZIP_CODE".equalsIgnoreCase((locationType))) {
            result.setCity(addressParts[0].trim());
            String[] stateZip =addressParts[1].trim().split(" ");
            if (stateZip.length == 2) {
                State state = stateService.findByAbbrOrFullName(stateZip[0].trim());
                result.setState(new KeyValueDto(state.getId(), state.getAbbr()));
                result.setZip(stateZip[1].trim());
            }
        }
        return result;
    }

    public OrganizationAddressService getOrganizationAddressService() {
        return organizationAddressService;
    }

    public void setOrganizationAddressService(OrganizationAddressService organizationAddressService) {
        this.organizationAddressService = organizationAddressService;
    }

    public Converter<Marketplace, Map<String, String[]>> getSelectedInNetworkInsurancePlansConverter() {
        return selectedInNetworkInsurancePlansConverter;
    }

    public void setSelectedInNetworkInsurancePlansConverter(Converter<Marketplace, Map<String, String[]>> selectedInNetworkInsurancePlansConverter) {
        this.selectedInNetworkInsurancePlansConverter = selectedInNetworkInsurancePlansConverter;
    }


    private List<PrimaryFocusKeyValueDto> getFilteredPrimaryFocusKeyValueDtos(final List<Long> primaryFocusIds, List<PrimaryFocusKeyValueDto> source) {
        if (CollectionUtils.isEmpty(primaryFocusIds)) {
            return Collections.emptyList();
        }
        List<PrimaryFocusKeyValueDto> filteredList = FluentIterable.from(source).filter(new Predicate<PrimaryFocusKeyValueDto>() {
            @Override
            public boolean apply(PrimaryFocusKeyValueDto primaryFocusKeyValueDto) {
                return primaryFocusIds.contains(primaryFocusKeyValueDto.getPrimaryFocusId());
            }
        }).toList();
        return filteredList;
    }

    @Override
    public List<List<PrimaryFocusKeyValueDto>> getFilteredCommunityTypesListofLists(List<Long> primaryFocusIds) {
        final List<PrimaryFocusKeyValueDto> allCommunityTypes = communityTypeDtoSupplier.get();
        return makeFilteredListOfLists(primaryFocusIds,allCommunityTypes);
    }

    @Override
    public List<List<PrimaryFocusKeyValueDto>> getFilteredServiceTreatmentApproachListofLists(List<Long> primaryFocusIds) {
        final List<PrimaryFocusKeyValueDto> allServiceTreatmentApproaches = servicesTreatmentApproachDtoSupplier.get();
        return makeFilteredListOfLists(primaryFocusIds,allServiceTreatmentApproaches);
    }

    private List<List<PrimaryFocusKeyValueDto>> makeFilteredListOfLists(List<Long> primaryFocusIds, List<PrimaryFocusKeyValueDto> entityToBeFiltered){
        List<PrimaryFocusKeyValueDto> fullList=getFilteredPrimaryFocusKeyValueDtos(primaryFocusIds, entityToBeFiltered);
        Map<Long,List<PrimaryFocusKeyValueDto>> aggregatedMap = new HashMap<Long,List<PrimaryFocusKeyValueDto>>();
        for(PrimaryFocusKeyValueDto element : fullList) {
            if(aggregatedMap.containsKey(element.getPrimaryFocusId())) {
                aggregatedMap.get(element.getPrimaryFocusId()).add(element);
            }
            else {
                List<PrimaryFocusKeyValueDto> newList=new ArrayList<PrimaryFocusKeyValueDto>();
                newList.add(element);
                aggregatedMap.put(element.getPrimaryFocusId(), newList);
            }
        }
        List<List<PrimaryFocusKeyValueDto>> finalList=new ArrayList<List<PrimaryFocusKeyValueDto>>(aggregatedMap.values());
        EntityListUtils.moveKeyValueDtoinListofListsToEnd(finalList, "other");
        return finalList;
    }

    public List<KeyValueDto> removeDuplicatesKeyValueDtos(List<KeyValueDto> source){
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        List<KeyValueDto> newList = new ArrayList<KeyValueDto>();
        for (KeyValueDto element : source) {
            if (!EntityListUtils.checkByLabelIfListContains(newList,element)) {
                newList.add(element);
            }
        }
        EntityListUtils.moveKeyValueDtoItemToEnd(newList, "other");
        return newList;
    }

    @Override
    public List<Long> getCommunityTypeIdsWithSameDisplayName(List<Long> selectedCommunityTypeIds){
    	List<PrimaryFocusKeyValueDto> allCommunityTypes =communityTypeDtoSupplier.get();
    	return getIdsWithSameDisplayName(allCommunityTypes,selectedCommunityTypeIds);
    }

    @Override
    public List<Long> getServiceTreatmentApproachIdsWithSameDisplayName(List<Long> selectedServicesTreatmentApproachIds){
    	final List<PrimaryFocusKeyValueDto> allServiceTreatmentApproaches = servicesTreatmentApproachDtoSupplier.get();
    	return getIdsWithSameDisplayName(allServiceTreatmentApproaches,selectedServicesTreatmentApproachIds);
    }

    private List<Long> getIdsWithSameDisplayName(List<PrimaryFocusKeyValueDto> allPrimaryFocusKeyValueDtoList,List<Long> selectedIds){
    	List<String> selectedEntityNames = new ArrayList<String>();
    	for (PrimaryFocusKeyValueDto entity : allPrimaryFocusKeyValueDtoList) {
    		if(selectedIds.contains(entity.getId())) {
    			selectedEntityNames.add(entity.getLabel());
    		}
    	}
    	List<Long> listOfEntityIdsWithSameDisplayName = new ArrayList<Long>();
    	for (PrimaryFocusKeyValueDto entity : allPrimaryFocusKeyValueDtoList) {
    		if(selectedEntityNames.contains(entity.getLabel())) {
    			listOfEntityIdsWithSameDisplayName.add(entity.getId());
    		}
    	}
    	return listOfEntityIdsWithSameDisplayName;
    }
}