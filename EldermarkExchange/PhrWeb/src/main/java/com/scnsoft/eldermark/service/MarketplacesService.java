package com.scnsoft.eldermark.service;

import com.google.maps.model.LatLng;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.marketplace.MarketplaceCustomDao;
import com.scnsoft.eldermark.dao.marketplace.MarketplaceDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.marketplace.CommunityType;
import com.scnsoft.eldermark.entity.marketplace.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.marketplace.Marketplace;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import com.scnsoft.eldermark.services.direct.DirectAttachment;
import com.scnsoft.eldermark.services.direct.MailAccountDetailsFactory;
import com.scnsoft.eldermark.services.marketplace.MapsService;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Service
@Transactional
public class MarketplacesService extends BasePhrService {

    private final static Long LEVEL_OF_CARE_VIEW_ORDER = 1l;
    private final static Long SERVICES_TREATMENT_APPROACHES_VIEW_ORDER = 2l;
    private final static Long EMERGENCY_SERVICE_VIEW_ORDER = 3l;
    private final static Long ANCILLARY_SERVICE_VIEW_ORDER = 4l;
    private final static Long AGE_GROUP_VIEW_ORDER = 5l;
    private final static Long LANGUAGE_SERVICE_VIEW_ORDER = 6l;
    private final static Long INSURANCE_PLAN_VIEW_ORDER = 7l;

    private final static String LEVEL_OF_CARE_VIEW_TITLE = "Levels Of Care";
    private final static String SERVICES_TREATMENT_APPROACHES_VIEW_TITLE = "Services/Treatment Approaches";
    private final static String EMERGENCY_SERVICE_VIEW_TITLE = "Urgent Care";
    private final static String ANCILLARY_SERVICE_VIEW_TITLE = "Ancillary Services";
    private final static String AGE_GROUP_VIEW_TITLE = "Age Groups Accepted";
    private final static String LANGUAGE_SERVICE_VIEW_TITLE = "Language Services";
    private final static String INSURANCE_PLAN_VIEW_TITLE = "Payment/Insurance Accepted";

    private final static String LEVEL_OF_CARE_VIEW_KEY = "LEVELS_OF_CARE";
    private final static String SERVICES_TREATMENT_APPROACHES_VIEW_KEY = "SERVICES_TREATMENT_APPROACHES";
    private final static String EMERGENCY_SERVICE_VIEW_KEY = "EMERGENCY_SERVICES";
    private final static String ANCILLARY_SERVICE_VIEW_KEY = "ANCILLARY_SERVICES";
    private final static String AGE_GROUP_VIEW_KEY = "AGE_GROUPS_ACCEPTED";
    private final static String LANGUAGE_SERVICE_VIEW_KEY = "LANGUAGE_SERVICES";
    private final static String INSURANCE_PLAN_VIEW_KEY = "PAYMENT_INSURANCE_ACCEPTED";

    @Autowired
    private MarketplaceDao marketplaceDao;

    @Autowired
    private StateService stateService;

    @Autowired
    private MapsService mapsService;

    @Autowired
    private MarketplaceCustomDao marketplaceCustomDao;

    @Autowired
    private MailAccountDetailsFactory mailAccountDetailsFactory;

    @Autowired
    private DirectMessagesFacade directMessagesFacade;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private ResidentDao residentDao;

    public Page<MarketplaceListInfoDto> getDiscoverableMarketplaces(Long careTypeId, List<Long> communityTypeIds, Boolean isNotHavingServicesIncluded, List<Long> servicesTreatmentApproachesIds,
                                                                    List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices,
                                                                    String location, String locationType, String searchText, Double userLatitude, Double userLongitude, Pageable pageable) {
        AddressDto address = null;
        if (location != null) {
            address = parseLocation(location, locationType);
        }
        Page<Marketplace> marketplaces = marketplaceCustomDao.filterMarketplaces(Collections.singletonList(careTypeId), communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds, inNetworkInsurancesIds, insurancePlansIds, emergencyServices, address, searchText, pageable, userLatitude, userLongitude);
        return convert(marketplaces, userLatitude, userLongitude, pageable);
    }

    public CountDto getDiscoverableMarketplacesCount(Long careTypeId, List<Long> communityTypeIds, Boolean isNotHavingServicesIncluded, List<Long> servicesTreatmentApproachesIds,
                                                     List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices,
                                                     String location, String locationType, String searchText) {
        AddressDto address = null;
        if (location != null) {
            address = parseLocation(location, locationType);
        }
        Pair<Long, Long> counts = marketplaceCustomDao.countMarketplaces(Collections.singletonList(careTypeId), communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds, inNetworkInsurancesIds, insurancePlansIds, emergencyServices, address, searchText);
        CountDto result = new CountDto();
        result.setCount(counts.getFirst());
        result.setTotalCount(counts.getSecond());
        return result;
    }

    public MarketplaceInfoDto getMarketplace(Long marketplaceId, Double userLatitude, Double userLongitude) {
        Marketplace marketplace = marketplaceDao.getOne(marketplaceId);
        return marketplace != null ? transformItem(marketplace, userLatitude, userLongitude) : null;
    }

    public MarketplaceServicesDto getMarketplacesServices(Long marketplaceId) {
        Marketplace marketplace = marketplaceDao.getOne(marketplaceId);
        return marketplace != null ? transformToServicesDto(marketplace) : null;
    }

    public void processNewAppointment(Long marketplaceId, Long userId, AppointmentCreateDto appointmentCreateDto) {
        Marketplace marketplace = marketplaceDao.getOne(marketplaceId);
        if (BooleanUtils.isNotTrue(marketplace.getAllowAppointments())) {
            throw new PhrException(PhrExceptionType.CANT_CREATE_APPOINTMENT);
        }

        Long residentId = getResidentIdOrThrow(userId);
        Resident resident = residentDao.get(residentId);
        String messageBody = createAppointmentSecureEmailMessageBody(marketplace, appointmentCreateDto, resident);
        DirectAccountDetails companyAccount = mailAccountDetailsFactory.createMarketplaceAccountDetails(marketplace);
        directMessagesFacade.sendMessage(marketplace.getSecureEmail(), "A new appointment has been requested via Simply Connect app.", messageBody, new ArrayList<DirectAttachment>(), companyAccount);
    }

    public Integer getMaxDaysToProcessAppointment(Long marketplaceId) {
        Marketplace marketplace = marketplaceDao.getOne(marketplaceId);
        Integer maxDaysToProcessAppointment = marketplace.getDatabase().getMaxDaysToProcessAppointment();
        return maxDaysToProcessAppointment != null ? maxDaysToProcessAppointment : 3;
    }

    private String createAppointmentSecureEmailMessageBody(Marketplace marketplace, AppointmentCreateDto appointmentCreateDto, Resident resident) {
        Map<String, String> config = new HashMap<>();
        config.put(DateTool.TIMEZONE_KEY, "CST");
        DateTool dateTool = new DateTool();
        dateTool.configure(config);
        Map<String, Object> model = new HashMap<>();
        MarketplaceInfoDto marketplaceInfoDto = new MarketplaceInfoDto();
        addBasicInfo(marketplace, null, null, marketplaceInfoDto);
        model.put("appointment", appointmentCreateDto);
        model.put("marketplace", marketplaceInfoDto);
        model.put("date", dateTool);
        model.put("ssnLast4", resident.getSsnLastFourDigits());
        if (resident.getBirthDate() != null) {
            model.put("dateOfBirth", resident.getBirthDate().getTime());
        }
        if (resident.getGender() != null) {
            model.put("gender", resident.getGender().getDisplayName());
        }
        model.put("patientName", resident.getFullName());
        if (CollectionUtils.isNotEmpty(appointmentCreateDto.getServicesNames())) {
            model.put("services", StringUtils.join(appointmentCreateDto.getServicesNames(), "; "));
        }
        final PersonTelecom emailTelecom = (PersonTelecom) CollectionUtils.find(new ArrayList<>(resident.getPerson().getTelecoms()), new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return PersonTelecomCode.EMAIL.name().equals(((PersonTelecom) o).getUseCode());
            }
        });

        if (emailTelecom != null) {
            model.put("patientEmail", emailTelecom.getValueNormalized());
        }

        Integer daysToContact = marketplace.getDatabase().getMaxDaysToProcessAppointment();
        model.put("daysToContact", daysToContact != null ? daysToContact : 3);
        return VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "velocity/newMarketplaceAppointmentSecureMessage.vm", "UTF-8", model);
    }

    private AddressDto parseLocation(String location, String locationType) {
        //TODO validate input
        String[] addressParts = location.split(",");
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
            String[] stateZip = addressParts[1].trim().split(" ");
            if (stateZip.length == 2) {
                State state = stateService.findByAbbrOrFullName(stateZip[0].trim());
                result.setState(new KeyValueDto(state.getId(), state.getAbbr()));
                result.setZip(stateZip[1].trim());
            }
        }
        return result;
    }

    private MarketplaceInfoDto transformItem(Marketplace marketplace, Double userLatitude, Double userLongitude) {
        MarketplaceInfoDto marketplaceInfoDto = new MarketplaceInfoDto();
        addBasicInfo(marketplace, userLatitude, userLongitude, marketplaceInfoDto);
        marketplaceInfoDto.setSummary(marketplace.getSummary());
        marketplaceInfoDto.setAllowAppointments(marketplace.getAllowAppointments() && StringUtils.isNotBlank(marketplace.getSecureEmail()));
        if (marketplace.getOrganization() != null) {
            marketplaceInfoDto.setPhone(marketplace.getOrganization().getPhone());
        }
        return marketplaceInfoDto;
    }

    private Page<MarketplaceListInfoDto> convert(Page<Marketplace> marketplaces, Double userLatitude, Double userLongitude, Pageable pageable) {
        List<Marketplace> marketplaceList = marketplaces.getContent();
        List<MarketplaceListInfoDto> marketplaceListInfoDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(marketplaceList)) {
            for (Marketplace marketplace : marketplaceList) {
                marketplaceListInfoDtoList.add(transformListItem(marketplace, userLatitude, userLongitude));
            }
        }
        return new PageImpl<>(marketplaceListInfoDtoList, pageable, marketplaces.getTotalElements());
    }

    private MarketplaceListInfoDto transformListItem(Marketplace marketplace, Double userLatitude, Double userLongitude) {
        MarketplaceListInfoDto marketplaceListInfoDto = new MarketplaceListInfoDto();
        addBasicInfo(marketplace, userLatitude, userLongitude, marketplaceListInfoDto);
        return marketplaceListInfoDto;
    }

    private void addBasicInfo(Marketplace marketplace, Double userLatitude, Double userLongitude, BasicMarketplaceInfoDto marketplaceListInfoDtoToAdd) {
        marketplaceListInfoDtoToAdd.setId(marketplace.getId());
        marketplaceListInfoDtoToAdd.setOrganizationName(marketplace.getDatabase().getName());
        if (marketplace.getOrganization() != null) {
            marketplaceListInfoDtoToAdd.setCommunityName(marketplace.getOrganization().getName());
            if (CollectionUtils.isNotEmpty(marketplace.getOrganization().getAddresses())) {
                Address organizationAddress = marketplace.getOrganization().getAddresses().get(0);
                AddressDto addressDto = new AddressDto();
                if (!StringUtils.isEmpty(organizationAddress.getState())) {
                    addressDto.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(organizationAddress.getState())));
                }
                addressDto.setCity(organizationAddress.getCity());
                addressDto.setStreet(organizationAddress.getStreetAddress());
                addressDto.setZip(organizationAddress.getPostalCode());
                marketplaceListInfoDtoToAdd.setAddress(addressDto.getDisplayAddress());
                LocationDto locationDto = getLocationDto(userLatitude, userLongitude, addressDto);
                marketplaceListInfoDtoToAdd.setLocation(locationDto);
            }
        }
        List<String> communityTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(marketplace.getCommunityTypes())) {
            for (CommunityType communityType : marketplace.getCommunityTypes()) {
                communityTypes.add(communityType.getDisplayName());
            }
        }
        marketplaceListInfoDtoToAdd.setCommunityTypes(communityTypes);
    }

    private LocationDto getLocationDto(Double userLatitude, Double userLongitude, AddressDto addressDto) {
        LocationDto locationDto = new LocationDto();
        LatLng location = mapsService.getCoordinatesByAddress(addressDto.getDisplayAddress());
        if (location != null) {
            locationDto.setLatitude(location.lat);
            locationDto.setLongitude(location.lng);
            if (userLatitude != null && userLongitude != null) {
                LatLng userLocation = new LatLng(userLatitude, userLongitude);
                Double distanceInMiles = mapsService.calculateDistance(location, userLocation);
                locationDto.setDistanceInMiles(distanceInMiles);
            }
        }
        return locationDto;
    }

    private MarketplaceServicesDto transformToServicesDto(Marketplace marketplace) {
        MarketplaceServicesDto marketplaceServicesDto = new MarketplaceServicesDto();
        marketplaceServicesDto.setId(marketplace.getId());
        marketplaceServicesDto.setLevelsOfCare(getMarketplaceServiceSectionDto(LEVEL_OF_CARE_VIEW_ORDER, marketplace.getLevelsOfCare(), LEVEL_OF_CARE_VIEW_TITLE, LEVEL_OF_CARE_VIEW_KEY));
        marketplaceServicesDto.setServicesTratmentApproaches(getMarketplaceServiceSectionDto(SERVICES_TREATMENT_APPROACHES_VIEW_ORDER, marketplace.getServicesTreatmentApproaches(), SERVICES_TREATMENT_APPROACHES_VIEW_TITLE, SERVICES_TREATMENT_APPROACHES_VIEW_KEY));
        marketplaceServicesDto.setEmergencyServices(getMarketplaceServiceSectionDto(EMERGENCY_SERVICE_VIEW_ORDER, marketplace.getEmergencyServices(), EMERGENCY_SERVICE_VIEW_TITLE, EMERGENCY_SERVICE_VIEW_KEY));
        marketplaceServicesDto.setAncillaryServices(getMarketplaceServiceSectionDto(ANCILLARY_SERVICE_VIEW_ORDER, marketplace.getAncillaryServices(), ANCILLARY_SERVICE_VIEW_TITLE, ANCILLARY_SERVICE_VIEW_KEY));
        marketplaceServicesDto.setAgeGroups(getMarketplaceServiceSectionDto(AGE_GROUP_VIEW_ORDER, marketplace.getAcceptedAgeGroups(), AGE_GROUP_VIEW_TITLE, AGE_GROUP_VIEW_KEY));
        marketplaceServicesDto.setLanguageServices(getMarketplaceServiceSectionDto(LANGUAGE_SERVICE_VIEW_ORDER, marketplace.getLanguageServices(), LANGUAGE_SERVICE_VIEW_TITLE, LANGUAGE_SERVICE_VIEW_KEY));

        MarketplaceServiceInsuranceSectionDto paymentInsurances = getMarketplaceServiceInsuranceSectionDto(marketplace);
        marketplaceServicesDto.setPaymentInsurances(paymentInsurances);
        return marketplaceServicesDto;
    }

    private MarketplaceServiceInsuranceSectionDto getMarketplaceServiceInsuranceSectionDto(Marketplace marketplace) {
        MarketplaceServiceInsuranceSectionDto paymentInsurances = new MarketplaceServiceInsuranceSectionDto();
        paymentInsurances.setOrder(INSURANCE_PLAN_VIEW_ORDER);
        paymentInsurances.setName(INSURANCE_PLAN_VIEW_TITLE);
        paymentInsurances.setKey(INSURANCE_PLAN_VIEW_KEY);
        List<MarketplaceServiceInsuranceSectionValueDto> insurancePlanDto = new ArrayList<>();
        MultiValueMap<String, InNetworkInsurance> insurancesByName = new LinkedMultiValueMap<>();
        MultiValueMap<InNetworkInsurance, InsurancePlan> plansByNetwork = new LinkedMultiValueMap<>();
        if (CollectionUtils.isNotEmpty(marketplace.getInsurancePlans())) {
            for (InsurancePlan insurancePlan : marketplace.getInsurancePlans()) {
                if (!plansByNetwork.containsKey(insurancePlan.getInNetworkInsurance())) {
                    insurancesByName.add(insurancePlan.getInNetworkInsurance().getDisplayName(), insurancePlan.getInNetworkInsurance());
                }
                plansByNetwork.add(insurancePlan.getInNetworkInsurance(), insurancePlan);
            }
        }
        if (CollectionUtils.isNotEmpty(marketplace.getInNetworkInsurances())) {
            for (InNetworkInsurance inNetworkInsurance : marketplace.getInNetworkInsurances()) {
                if (!plansByNetwork.containsKey(inNetworkInsurance)) {
                    insurancesByName.add(inNetworkInsurance.getDisplayName(), inNetworkInsurance);
                }
            }
        }
        List<String> insuranceNames = new ArrayList<>(insurancesByName.keySet());
        Collections.sort(insuranceNames);
        for (String insuranceName : insuranceNames) {
            List<InNetworkInsurance> insurances = insurancesByName.get(insuranceName);
            for (InNetworkInsurance insurance : insurances) {
                MarketplaceServiceInsuranceSectionValueDto dto = new MarketplaceServiceInsuranceSectionValueDto();
                dto.setName(insurance.getDisplayName());
                if (plansByNetwork.containsKey(insurance)) {
                    List<String> plans = new ArrayList<>();
                    for (InsurancePlan plan : plansByNetwork.get(insurance)) {
                        plans.add(plan.getDisplayName());
                    }
                    Collections.sort(plans);
                    dto.setPlans(plans);
                }
                insurancePlanDto.add(dto);
            }
        }
        paymentInsurances.setData(insurancePlanDto);
        return paymentInsurances;
    }


    private <T extends DisplayableNamedEntity> MarketplaceServiceSectionDto getMarketplaceServiceSectionDto(Long order, Set<T> entities, String title, String key) {
        MarketplaceServiceSectionDto marketplaceServiceSectionDto = new MarketplaceServiceSectionDto();
        marketplaceServiceSectionDto.setOrder(order);
        marketplaceServiceSectionDto.setName(title);
        marketplaceServiceSectionDto.setKey(key);
        marketplaceServiceSectionDto.setData(orderAndCreateServicesValueDtoList(new ArrayList<>(entities)));
        return marketplaceServiceSectionDto;
    }

    private <T extends DisplayableNamedEntity> List<MarketplaceServiceSectionValueDto> orderAndCreateServicesValueDtoList(List<T> entities) {
        List<MarketplaceServiceSectionValueDto> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entities)) {
            Collections.sort(entities, new Comparator<T>() {
                @Override
                public int compare(DisplayableNamedEntity o1, DisplayableNamedEntity o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });
            for (T entity : entities) {
                MarketplaceServiceSectionValueDto marketplaceServiceSectionValueDto = new MarketplaceServiceSectionValueDto();
                marketplaceServiceSectionValueDto.setName(entity.getDisplayName());
                result.add(marketplaceServiceSectionValueDto);
            }
        }
        return result;
    }
}
