package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.merging.MpiMergedResidentsService;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilter;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.exceptions.ResidentNotFoundException;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(readOnly = true)
@Service
public class ResidentFacadeImpl implements ResidentFacade {
    private static final Logger logger = LoggerFactory.getLogger(ResidentFacadeImpl.class);

    @Autowired
    private ResidentService residentService;

    @Autowired
    private MpiMergedResidentsService mpiMergedResidentsService;

    @Value("${patient.discovery.default.first.name}")
    private String defaultResidentFirstName;
    @Value("${patient.discovery.default.last.name}")
    private String defaultResidentLastName;
    @Value("${patient.discovery.default.gender}")
    private String defaultResidentGender;
    @Value("${patient.discovery.default.dateOfBirth}")
    private String defaultResidentDateOfBirth;
    @Value("${patient.discovery.default.ssn.last.4}")
    private String defaultResidentSsn;

    @Override
    public List<ResidentDto> getResidents(ResidentFilter filter, final Pageable pageable, boolean showSsn) {

        List<Resident> residents;
        if (pageable != null) {
            residents = residentService.getResidents(filter, pageable);
            // TODO return residents.size() for shortcircuiting total calculations
        } else {
            residents = residentService.getResidents(filter);
        }

        List<ResidentDto> residentDtos = new ArrayList<ResidentDto>();
        List<Long> residentIds = new ArrayList<Long>();
        int mergedResidentsTotal = 0;
        int probablyMatchedResidentsTotal = 0;

        for (Resident matchedResident : residents) {
            Long matchedResidentId = matchedResident.getId();

            residentIds.add(matchedResidentId);

            ResidentDto residentDto = createDto(matchedResident,showSsn);

            residentDtos.add(residentDto);

            // MergeStatus = null means "any"
            if (!MergeStatus.NOT_MERGED.equals(filter.getMergeStatus())) {
                Set<Resident> mergedResidents = residentService.getDirectMergedResidents(matchedResident);
                List<Resident> sortedMergedResidents = sort(mergedResidents, pageable);
                residentDtos.addAll(createMergedDtoList(matchedResidentId, sortedMergedResidents, true));

                List<Long> mergedResidentsIds = new ArrayList<Long>();
                CollectionUtils.collect(mergedResidents, new BeanToPropertyValueTransformer("id"), mergedResidentsIds);
                residentIds.addAll(mergedResidentsIds);

                if (mergedResidents.size()>0){
                    mergedResidentsTotal += mergedResidents.size();
                    residentDto.setHasMerged(true);
                }
            }

            if (MatchStatus.MAYBE_MATCHED.equals(filter.getMatchStatus())) {
                Collection<Resident> probablyMatchedResidents = residentService.getDirectProbablyMatchedResidents(matchedResident);
                if (CollectionUtils.isEmpty(probablyMatchedResidents)) {
                    // the returned list can be empty if a resident has "maybe matched" residents in DB but the acting employee has no access to them.
                    // In this case the resident should be excluded from results
                    residentDtos.remove(residentDtos.size() - 1);
                } else {
                    residentDtos.addAll(createProbablyMatchedDtoList(matchedResidentId, probablyMatchedResidents));

                    List<Long> probablyMatchedResidentsIds = new ArrayList<Long>(probablyMatchedResidents.size());
                    CollectionUtils.collect(probablyMatchedResidents, new BeanToPropertyValueTransformer("id"), probablyMatchedResidentsIds);
                    residentIds.addAll(probablyMatchedResidentsIds);

                    probablyMatchedResidentsTotal += probablyMatchedResidents.size();
                }
            }
        }

        logger.debug("Found {} residents for filter = {}. Including {} merged residents and {} \"maybe matched\" residents.",
                residentDtos.size(), filter.toString(), mergedResidentsTotal, probablyMatchedResidentsTotal);

        return residentDtos;
    }

    // utility method for partial sort of residents collection
    private static List<Resident> sort(Set<Resident> residents, final Pageable pageable) {
        final ArrayList<Resident> sorted = new ArrayList<Resident>(residents);
        if (pageable != null && pageable.getSort() != null) {
            Collections.sort(sorted, new Comparator<Resident>() {
                @Override
                public int compare(Resident o1, Resident o2) {
                    for (Sort.Order order : pageable.getSort()) {
                        int comparison = 0;
                        if ("lastName".equals(order.getProperty())) {
                            comparison = ObjectUtils.compare(o1.getLastName(), o2.getLastName());
                        } else if ("firstName".equals(order.getProperty())) {
                            comparison = ObjectUtils.compare(o1.getFirstName(), o2.getFirstName());
                        } else if ("genderDisplayName".equals(order.getProperty())) {
                            comparison = ObjectUtils.compare(o1.getGender(), o2.getGender());
                        } else if ("dateOfBirth".equals(order.getProperty())) {
                            comparison = ObjectUtils.compare(o1.getBirthDate(), o2.getBirthDate());
                        } else if ("organizationName".equals(order.getProperty())) {
                            comparison = ObjectUtils.compare(o1.getFacility(), o2.getFacility());
                        } else if ("databaseName".equals(order.getProperty())) {
                            comparison = ObjectUtils.compare(o1.getDatabase().getName(), o2.getDatabase().getName());
                        } else if ("dateCreated".equals(order.getProperty())) {
                            comparison = ObjectUtils.compare(o1.getDateCreated(), o2.getDateCreated());
                        }
                        if (comparison != 0) {
                            return Sort.Direction.ASC.equals(order.getDirection()) ? comparison : -comparison;
                        }
                    }
                    return 0;
                }
            });
        }

        return sorted;
    }

    @Override
    public List<ResidentDto> getResidents(ResidentFilter filter) {
        return getResidents(filter, null,true);
    }

    @Override
    public Long getResidentCount(ResidentFilter filter) {
        return residentService.getResidentCount(filter);
    }

    @Override
    public ResidentDto getResidentById(long residentId) {
        Resident resident = residentService.getResident(residentId);
        return createDto(resident,true);
    }

    @Override
    public List<ResidentDto> getResidentsByIds(Collection<Long> residentIds) {
        List<ResidentDto> residents = new ArrayList<ResidentDto>();

        for (Long id : residentIds) {
            try {
                Resident resident = residentService.getResident(id);
                residents.add(createDto(resident, true));
            } catch (ResidentNotFoundException ignored) {}
        }

        return residents;
    }

    @Override
    public Collection<ResidentDto> getMergedResidentsById(long residentId) {
        final Resident resident = residentService.getResident(residentId);
        Collection<Resident> residents = residentService.getDirectMergedResidents(resident);
        return createMergedDtoList(residentId, residents, true);
    }

    @Override
    public List<ResidentDto> getProbablyMatchedResidentsById(long residentId) {
        List<ResidentDto> residentDtos = new ArrayList<ResidentDto>();

        Resident resident = residentService.getResident(residentId);
        residentDtos.add(createDto(resident,true));

        Collection<Resident> residents = residentService.getDirectProbablyMatchedResidents(resident);
        if (CollectionUtils.isEmpty(residents)) {
            return Collections.emptyList();
        } else {
            residentDtos.addAll(createProbablyMatchedDtoList(residentId, residents));
            return residentDtos;
        }
    }

    @Override
    public void updateMatchedResidents(Map<Long, Boolean> residents) {
        List<Long> residentsToMatch = new ArrayList<Long>();
        List<Long> residentsToUnmatch = new ArrayList<Long>();

        for (Map.Entry<Long, Boolean> entry : residents.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                residentsToMatch.add(entry.getKey());
            } else {
                residentsToUnmatch.add(entry.getKey());
            }
        }
        if (!CollectionUtils.isEmpty(residentsToMatch)) {
            residentService.matchAndMergeResidents(residentsToMatch);
        }
        if (!CollectionUtils.isEmpty(residentsToUnmatch)) {
            //noinspection unchecked
            residentService.unmatchResidents(ListUtils.union(residentsToMatch, residentsToUnmatch), residentsToUnmatch);
        }
    }

    @Override
    public boolean assertHashKey(long residentId, String providedHashKey) {
        if (providedHashKey == null) {
            return false;
        }

        Resident resident = residentService.getResident(residentId);
        if (resident == null) {
            return false;
        }

        return providedHashKey.equals(resident.getHashKey());
    }

    @Override
    public ResidentDto getDefaultResident() {
        ResidentDto defaultResident = new ResidentDto();

        defaultResident.setFirstName(defaultResidentFirstName);
        defaultResident.setLastName(defaultResidentLastName);

        if(!StringUtils.isBlank(defaultResidentGender)) {
            defaultResident.setGender(Gender.getGenderByCode(defaultResidentGender));
        }

        if(!StringUtils.isBlank(defaultResidentDateOfBirth)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                defaultResident.setDateOfBirth(format.parse(defaultResidentDateOfBirth));
            } catch (Exception ignored) {
            }
        }
        defaultResident.setSsn(defaultResidentSsn);

        return defaultResident;
    }

    private List<ResidentDto> createMergedDtoList(Long residentId, Iterable<Resident> mergedResidents, boolean initMatchedAutomatically) {
        List<ResidentDto> residentDtoList = new ArrayList<ResidentDto>();

        for (Resident resident : mergedResidents) {
            ResidentDto residentDto = createDto(resident,false);

            residentDto.setMergedId(residentId);

            if (initMatchedAutomatically) {
                boolean matchedAutomatically = mpiMergedResidentsService.areMergedAutomatically(resident, residentId);
                if (!matchedAutomatically) {
                    matchedAutomatically = isMatchedAutomaticallyWithAnyOf(resident.getId(), mergedResidents);
                }
                residentDto.setMatchedAutomatically(matchedAutomatically);
            }

            residentDtoList.add(residentDto);
        }

        return residentDtoList;
    }

    private boolean isMatchedAutomaticallyWithAnyOf(Long residentId, Iterable<Resident> mergedResidents) {
        for (Resident mergedResident : mergedResidents) {
            if (!residentId.equals(mergedResident.getId())) {
                boolean matchedAutomatically = mpiMergedResidentsService.areMergedAutomatically(mergedResident, residentId);
                if (matchedAutomatically) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<ResidentDto> createProbablyMatchedDtoList(Long residentId, Iterable<Resident> probablyMatchedResidents) {
        List<ResidentDto> residentDtoList = new ArrayList<ResidentDto>();

        for (Resident resident : probablyMatchedResidents) {
            ResidentDto residentDto = createDto(resident,true);
            residentDto.setProbablyMatchedId(residentId);
            residentDtoList.add(residentDto);
        }

        return residentDtoList;
    }

    private static ResidentDto createDto(Resident resident, boolean showSsn) {
        ResidentDto residentDto = new ResidentDto();
        residentDto.setId(resident.getId().toString());
        residentDto.setResidentNumber(resident.getLegacyId());
        if (showSsn) {
            residentDto.setSsn(resident.getSocialSecurity());
        } else {
            residentDto.setSsn("###-##-" + resident.getSsnLastFourDigits());
        }
        residentDto.setHashKey(resident.getHashKey());

        CcdCode genderCcdCode = resident.getGender(); //May be null if Gender_CCDID isn't set in 4D database
        if (genderCcdCode != null) {
            residentDto.setGender(Gender.getGenderByCode(genderCcdCode.getCode()));
        }
        residentDto.setDateOfBirth(resident.getBirthDate());
        residentDto.setFirstName(resident.getFirstName());
        residentDto.setLastName(resident.getLastName());
        residentDto.setMiddleName(resident.getMiddleName());

        residentDto.setSearchScope(SearchScope.ELDERMARK);

        for (Address address : resident.getPerson().getAddresses()) {
            if (address.getPostalAddressUse() == null || "HP".equals(address.getPostalAddressUse())) {
                residentDto.setStreetAddress(address.getStreetAddress());
                residentDto.setCity(address.getCity());
                residentDto.setState(address.getState());
                residentDto.setPostalCode(address.getPostalCode());
            }
        }

        for (Telecom telecom : resident.getPerson().getTelecoms()) {
            if ("EMAIL".equals(telecom.getUseCode())) {
                residentDto.setEmail(telecom.getValue());
            } else if ("HP".equals(telecom.getUseCode()) || "WP".equals(telecom.getUseCode()) || "MC".equals(telecom.getUseCode())) {
                residentDto.setPhone(telecom.getValue());
            }
        }

        Organization facility = resident.getFacility();
        if (facility != null) {
            residentDto.setOrganizationId(facility.getLegacyId());
            residentDto.setOrganizationName(facility.getName());
        }
        residentDto.setDatabaseId(resident.getDatabase().getId());
        residentDto.setDatabaseName(resident.getDatabase().getName());

        residentDto.setDateCreated(resident.getDateCreated());

        return residentDto;
    }

    public void setResidentService(ResidentService residentService) {
        this.residentService = residentService;
    }

    public void setDefaultResidentFirstName(String defaultResidentFirstName) {
        this.defaultResidentFirstName = defaultResidentFirstName;
    }

    public void setDefaultResidentLastName(String defaultResidentLastName) {
        this.defaultResidentLastName = defaultResidentLastName;
    }

    public void setDefaultResidentGender(String defaultResidentGender) {
        this.defaultResidentGender = defaultResidentGender;
    }

    public void setDefaultResidentDateOfBirth(String defaultResidentDateOfBirth) {
        this.defaultResidentDateOfBirth = defaultResidentDateOfBirth;
    }

    public void setDefaultResidentSsn(String defaultResidentSsn) {
        this.defaultResidentSsn = defaultResidentSsn;
    }
}
