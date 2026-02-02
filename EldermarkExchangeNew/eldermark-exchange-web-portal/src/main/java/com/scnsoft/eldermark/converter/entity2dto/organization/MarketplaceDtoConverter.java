package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ReferralSecurityFieldsAware;
import com.scnsoft.eldermark.dto.InsuranceNetworkDto;
import com.scnsoft.eldermark.dto.InsurancePlanDto;
import com.scnsoft.eldermark.dto.MarketplaceDto;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.InsurancePlan;
import com.scnsoft.eldermark.entity.LanguageService;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.security.ReferralSecurityService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.service.MarketplaceRatingServiceImpl.RATING_SERVICE_TYPE_KEY;

@Component
public class MarketplaceDtoConverter implements Converter<Pair<Marketplace, Long>, MarketplaceDto> {

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ReferralSecurityService referralSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private SavedMarketplaceService savedMarketplaceService;

    @Autowired
    private MarketplaceRatingService marketplaceRatingService;

    @Override
    public MarketplaceDto convert(Pair<Marketplace, Long> source) {
        MarketplaceDto result = new MarketplaceDto();
        var marketplace = source.getFirst();
        result.setId(marketplace.getId());
        result.setConfirmVisibility(marketplace.getDiscoverable());
        result.setServicesSummaryDescription(marketplace.getSummary());
        result.setLanguageIds(CollectionUtils.emptyIfNull(marketplace.getLanguageServices()).stream()
                .sorted(Comparator.comparing(LanguageService::getDisplayName)).map(LanguageService::getId)
                .collect(Collectors.toList()));
        result.setServiceCategoryIds(CollectionUtils.emptyIfNull(marketplace.getServiceCategories()).stream()
                .sorted(Comparator.comparing(ServiceCategory::getDisplayName)).map(ServiceCategory::getId)
                .collect(Collectors.toList()));
        result.setServiceIds(CollectionUtils.emptyIfNull(marketplace.getServiceTypes())
                .stream().sorted(Comparator.comparing(ServiceType::getDisplayName))
                .map(ServiceType::getId).collect(Collectors.toList()));

        if (marketplace.getCommunityId() != null) {
            var referralEmails = marketplace.getReferralEmails();
            result.setReferralEmails(CollectionUtils.isNotEmpty(referralEmails) ? referralEmails :
                    marketplace.getCommunity().getEmail() != null ? List.of(marketplace.getCommunity().getEmail()) : null);

            var networkIds = CareCoordinationUtils.toIdsSet(partnerNetworkService.findNetworksWithAllCommunities(marketplace.getCommunityId()));
            var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

            var referralClientId = source.getSecond();
            var clientCommunityId = referralClientId != null ? clientService.findById(referralClientId, CommunityIdAware.class).getCommunityId() : null;

            if (referralClientId != null && clientCommunityId == null) {
                //normally shouldn't happen
                result.setIsReferralEnabled(false);
                result.setCanAddReferral(false);
                return result;
            }

            if (clientCommunityId != null) {
                result.setIsReferralEnabled(marketplace.getCommunity().isReceiveNonNetworkReferrals() ||
                        partnerNetworkService.areInSameNetwork(marketplace.getCommunityId(), clientCommunityId));
            } else {
                result.setIsReferralEnabled(marketplace.getCommunity().isReceiveNonNetworkReferrals() ||
                        communityService.existAllowedReferralMarketplaceCommunitiesWithinAnyNetworks(permissionFilter, marketplace.getCommunityId(), networkIds));
            }

            if (result.getIsReferralEnabled()) {
                if (referralClientId != null) {
                    result.setCanAddReferral(referralSecurityService.canAdd(new ReferralSecurityFieldsAware() {
                        @Override
                        public List<Long> getSharedCommunityIds() {
                            return null;
                        }

                        @Override
                        public Long getReferringCommunityId() {
                            return clientCommunityId;
                        }

                        @Override
                        public Long getMarketplaceCommunityId() {
                            return marketplace.getCommunityId();
                        }

                        @Override
                        public List<Long> getServices() {
                            return ANY_SERVICES;
                        }

                        @Override
                        public Long getClientId() {
                            return referralClientId;
                        }
                    }));
                } else {
                    result.setCanAddReferral(communityService.isAccessibleReferralMarketplaceCommunity(permissionFilter, marketplace.getCommunity()));
                }
            } else {
                result.setCanAddReferral(false);
            }
        }
        result.setIsSaved(savedMarketplaceService.isExists(loggedUserService.getCurrentEmployeeId(), marketplace.getId()));
        if (Stream.ofNullable(marketplace.getServiceTypes()).flatMap(List::stream).anyMatch(type -> RATING_SERVICE_TYPE_KEY.equals(type.getKey()))) {
            result.setRating(marketplaceRatingService
                    .getRatingByName(marketplace.getCommunity() != null ? marketplace.getCommunity().getName() : marketplace.getOrganization().getName()).orElse(0));
        }
        return result;
    }

    private List<InsuranceNetworkDto> convertCommunityNetworksWithPlans(List<InNetworkInsurance> insurances,
                                                                        List<InsurancePlan> plans) {
        HashMap<InsuranceNetworkDto, List<InsurancePlanDto>> uniqueInsurancesWithPlans = new HashMap<>();
        CollectionUtils.emptyIfNull(insurances).forEach(inNetworkInsurance -> {
            uniqueInsurancesWithPlans.put(new InsuranceNetworkDto(inNetworkInsurance.getId(),
                            inNetworkInsurance.getDisplayName(), inNetworkInsurance.getKey(), inNetworkInsurance.getPopular()),
                    new ArrayList<>());
        });
        CollectionUtils.emptyIfNull(plans).forEach(plan -> {
            InsuranceNetworkDto planNetwork = new InsuranceNetworkDto(plan.getInNetworkInsuranceId(),
                    plan.getInNetworkInsurance().getDisplayName(), plan.getInNetworkInsurance().getKey(),
                    plan.getInNetworkInsurance().getPopular());
            if (!uniqueInsurancesWithPlans.containsKey(planNetwork)) {
                uniqueInsurancesWithPlans.put(planNetwork, new ArrayList<>());
            }
            uniqueInsurancesWithPlans.get(planNetwork)
                    .add(new InsurancePlanDto(plan.getId(), plan.getDisplayName(), plan.getKey(), plan.getPopular()));
        });
        uniqueInsurancesWithPlans.forEach(
                (insuranceNetworkDto, insurancePlanDtos) -> insuranceNetworkDto.setPaymentPlans(insurancePlanDtos));
        return new ArrayList<>(uniqueInsurancesWithPlans.keySet());
    }

}
