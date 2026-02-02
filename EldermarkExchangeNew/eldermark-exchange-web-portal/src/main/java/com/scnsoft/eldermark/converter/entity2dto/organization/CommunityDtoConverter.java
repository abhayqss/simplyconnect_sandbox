package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.docutrack.DocutrackPharmacyConfigDto;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import com.scnsoft.eldermark.entity.BaseAttachment;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.AffiliatedOrganizationService;
import com.scnsoft.eldermark.service.FeaturedServiceProviderService;
import com.scnsoft.eldermark.service.CommunityHieConsentPolicyService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.service.docutrack.DocutrackService;
import com.scnsoft.eldermark.service.security.CommunitySecurityService;
import com.scnsoft.eldermark.service.security.CommunityHieConsentPolicySecurityService;
import com.scnsoft.eldermark.service.security.DocutrackSecurityService;
import com.scnsoft.eldermark.service.security.MarketplaceCommunitySecurityService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityDtoConverter implements Converter<Community, CommunityDto> {

    @Autowired
    private StateService stateService;

    @Autowired
    private CommunitySecurityService communitySecurityService;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @Autowired
    private AffiliatedOrganizationService affiliatedOrganizationService;

    @Autowired
    private AffiliatedInfoConverter affiliatedInfoConverter;

    @Autowired
    private ListAndItemConverter<BaseAttachment, BaseAttachmentDto> baseAttachmentDtoConverter;

    @Autowired
    private Converter<X509Certificate, CertificateInfoDto> certificateInfoDtoConverter;

    @Autowired
    private DocutrackService docutrackService;

    @Autowired
    private DocutrackSecurityService docutrackSecurityService;

    @Autowired
    private Converter<Address, String> displayAddressConverter;

    @Autowired
    private FeaturedServiceProviderService featuredServiceProviderService;

    @Autowired
    private CommunityHieConsentPolicySecurityService communityHieConsentPolicySecurityService;

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Autowired
    private Converter<FeaturedServiceProvider, FeaturedServiceProviderDto> featuredServiceProviderDtoConverter;

    @Override
    public CommunityDto convert(Community source) {
        var target = new CommunityDto();
        fillData(source, target);
        return target;
    }

    public void fillData(Community source, CommunityDto target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setOid(source.getOid());
        target.setLogoName(source.getMainLogoPath());
        //target.setCommunityLogo(communityService.convertFileIntoBytes(source.getMainLogoPath()));
        target.setOrganizationId(source.getOrganizationId());
        target.setOrganizationName(source.getOrganization().getName());
        target.setIsSharingData(source.getIsSharingData());
        target.setLicenseNumber(source.getLicenseNumber());
        if (CollectionUtils.isNotEmpty(source.getAddresses())) {
            var communityAddress = source.getAddresses().get(0);
            if (!StringUtils.isEmpty(communityAddress.getState())) {
                var state = stateService.findByAbbr(communityAddress.getState());
                if (state != null) {
                    target.setStateId(state.getId());
                }
            }
            target.setCity(communityAddress.getCity());
            target.setStreet(communityAddress.getStreetAddress());
            target.setZipCode(communityAddress.getPostalCode());
            target.setLocation(getLocationDto(communityAddress.getLongitude(), communityAddress.getLatitude()));

            target.setDisplayAddress(displayAddressConverter.convert(communityAddress));
        }

        communityHieConsentPolicyService.findByCommunityIdAndArchived(source.getId(), false)
                .ifPresent(policy -> {
                    target.setHieConsentPolicyName(policy.getType());
                    target.setHieConsentPolicyTitle(policy.getType().getDisplayName());
                });

        target.setCanEditDocutrack(docutrackSecurityService.canConfigureDocutrackInOrg(source.getOrganizationId()));

        var canConfigureMarketplace = marketplaceCommunitySecurityService.canConfigure(source.getId());
        target.setCanEditAllowExternalInboundReferrals(canConfigureMarketplace);
        target.setCanEditMarketplaceReferralEmails(canConfigureMarketplace);
        target.setCanEditConfirmMarketplaceVisibility(canConfigureMarketplace);
        target.setCanEditFeaturedServiceProviders(marketplaceCommunitySecurityService.canEditFeaturedPartnerProviders(source.getId(), source.getOrganizationId()));

        target.setCanEdit(communitySecurityService.canEdit(source.getId()));
        target.setAllowExternalInboundReferrals(source.isReceiveNonNetworkReferrals());
        target.setCanEditHieConsentPolicy(communityHieConsentPolicySecurityService.canEdit(source.getId()));

        target.setAffiliationPrimary(getAffiliationPrimary(source.getId()));
        target.setAffiliationAffiliated(getAffiliationAffiliated(source.getId()));

        target.setPictures(baseAttachmentDtoConverter.convertList(source.getPictures()));
        target.setNumberOfBeds(source.getNumberOfBeds());
        target.setNumberOfVacantBeds(source.getNumberOfVacantBeds());
        target.setWebsiteUrl(source.getWebsiteUrl());

        fillFeaturedServiceProviders(source, target);

        fillDocutrackConfig(source, target);
        fillSecurityConfig(source, target);
    }

    private void fillFeaturedServiceProviders(Community source, CommunityDto target) {
        List<FeaturedServiceProviderDto> providerDtos = featuredServiceProviderService.fetchServiceProvidersByCommunityId(source.getId())
                .stream()
                .map(featuredServiceProviderDtoConverter::convert)
                .collect(Collectors.toList());
        target.setFeaturedServiceProviders(providerDtos);
    }

    //todo add distance
    private LocationWithDistanceDto getLocationDto(Double longitude, Double latitude) {
        final LocationWithDistanceDto locationDto = new LocationWithDistanceDto();
        locationDto.setLongitude(longitude);
        locationDto.setLatitude(latitude);
        return locationDto;
    }

    private List<AffiliationInfoItemDto> getAffiliationPrimary(Long communityId) {
        //current community is affiliated
        var primaryData = affiliatedOrganizationService.getAllForAffiliatedCommunityId(communityId);
        return affiliatedInfoConverter.convertAffiliationInfo(primaryData,
                AffiliatedOrganization::getPrimaryOrganizationId,
                AffiliationInfoItemDto::new,
                affiliatedInfoConverter.baseInfoItemFiller(primaryData,
                        AffiliatedOrganization::getPrimaryOrganizationId,
                        AffiliatedOrganization::getPrimaryCommunityId)
        );
    }

    private List<AffiliationInfoItemDto> getAffiliationAffiliated(Long communityId) {
        //current community is primary
        var affiliatedData = affiliatedOrganizationService.getAllForPrimaryCommunityId(communityId);
        return affiliatedInfoConverter.convertAffiliationInfo(affiliatedData,
                AffiliatedOrganization::getAffiliatedOrganizationId,
                AffiliationInfoItemDto::new,
                affiliatedInfoConverter.baseInfoItemFiller(affiliatedData,
                        AffiliatedOrganization::getAffiliatedOrganizationId,
                        AffiliatedOrganization::getAffiliatedCommunityId)
        );
    }

    private void fillDocutrackConfig(Community source, CommunityDto target) {
        var config = new DocutrackPharmacyConfigDto();
        config.setIsIntegrationEnabled(Boolean.TRUE.equals(source.getIsDocutrackPharmacy()));
        config.setClientType(source.getDocutrackClientType());
        config.setBusinessUnitCodes(new ArrayList<>(CollectionUtils.emptyIfNull(source.getBusinessUnitCodes())));
        config.setServerDomain(source.getDocutrackServerDomain());

        if (source.getDocutrackServerCertificateSha1() != null) {
            var cert = docutrackService.getConfiguredCertificate(source).orElseThrow();
            config.setConfiguredCertificate(certificateInfoDtoConverter.convert(cert));
        }

        if (StringUtils.isNotEmpty(source.getDocutrackServerDomain())) {
            try {
                docutrackService.loadServerCertIfSelfSigned(source.getDocutrackServerDomain())
                        .map(certificateInfoDtoConverter::convert)
                        .ifPresent(config::setServerCertificate);
            } catch (ValidationException e) {
                config.setDocutrackError(e.getMessage());
            }
        }

        target.setDocutrackPharmacyConfig(config);
    }

    private void fillSecurityConfig(Community source, CommunityDto target) {
        if (source.getOrganization().isSignatureEnabled()) {
            target.setSignatureConfig(new CommunitySignatureConfigDto());
            target.getSignatureConfig().setCanEdit(communitySecurityService.canEditSignatureConfig(source.getId()));
            target.getSignatureConfig().setIsPinEnabled(source.getIsSignaturePinEnabled());
        }
    }
}
