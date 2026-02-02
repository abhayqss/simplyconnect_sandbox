package com.scnsoft.eldermark.services.marketplace;

import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyTwoValuesDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyTwoValuesDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryFocusKeyValueDto;
import com.scnsoft.eldermark.shared.marketplace.BasicMarketplaceInfoDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryFocusKeyValueDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author phomal
 * Created by phomal on 11/27/2017.
 */
public interface MarketplaceService {

    List<KeyValueDto> getPrimaryFocuses();
    List<KeyValueDto> getCommunityTypes();
    List<KeyValueDto> getLevelsOfCare();
    List<KeyValueDto> getAgeGroups();
    List<KeyValueDto> getServicesTreatmentApproaches();
    List<KeyValueDto> getEmergencyServices();
    List<KeyValueDto> getLanguageServices();
    List<KeyValueDto> getAncillaryServices();

    List<KeyValueDto> getInNetworkInsurances();
    List<KeyValueDto> getNetworkInsurancesGroupWithoutName();
    List<KeyValueDto> getPopularInNetworkInsurances();
    <T extends KeyValueDto> List<KeyValueDto> getInNetworkInsurancesExceptSection0();

    Map<Long, List<AlphabetableKeyTwoValuesDto>> getInsurancePlans();
    Map<Long, Collection<KeyTwoValuesDto>> getPopularInsurancePlans();

    MarketplaceDto getMarketplaceByOrganizationId(Long orgId);
    MarketplaceDto getMarketplaceByCommunityId(Long commId);

    MarketplaceDto updateForOrganization(Long orgId, MarketplaceDto marketplaceDto);
    MarketplaceDto updateForCommunity(Long orgId, Long commId, MarketplaceDto marketplaceDto);

    void deleteForOrganization(Long orgId);
    void deleteForCommunity(Long communityId);

    List<KeyValueDto> searchInNetworkInsurance(String query);
//    List<KeyValueDto> getPopularInNetworkInsurances();

    Page<BasicMarketplaceInfoDto> getDiscoverableMarketplaces(List<Long> careTypeId, List<Long> communityTypeIds, List<Long> servicesTreatmentApproachesIds,
                                                              List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices,
                                                              String location, String locationType, String searchText, Double userLatitude, Double userLongitude, Pageable pageable);

    List<MarketplaceInfoDto> getMarketplacesByIds(List<Long>ids);

    MarketplaceDetailsDto getMarketplaceDetails(Long id);

    List<List<PrimaryFocusKeyValueDto>> getFilteredCommunityTypesListofLists(List<Long> primaryFocusIds);
    List<List<PrimaryFocusKeyValueDto>> getFilteredServiceTreatmentApproachListofLists(List<Long> primaryFocusIds);

    List<KeyValueDto> removeDuplicatesKeyValueDtos(List<KeyValueDto> source);

    List<Long> getCommunityTypeIdsWithSameDisplayName(List<Long> selectedCommunityTypeIds);
    List<Long> getServiceTreatmentApproachIdsWithSameDisplayName(List<Long> selectedServiceTreatmentApproachIds);
}