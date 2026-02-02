package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.CommunityDto;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.security.DocutrackSecurityService;
import com.scnsoft.eldermark.service.security.MarketplaceCommunitySecurityService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class CommunityEntityConverter implements ItemConverter<CommunityDto, Community> {

    private static final String CARE_COORDINATION_LEGACY_TABLE = "COMPANY";
    private static final boolean DEFAULT_RECEIVE_NON_NETWORK_REFERRALS = false;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityAddressEntityConverter communityAddressEntityConverter;

    @Autowired
    private DocutrackSecurityService docutrackSecurityService;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @Override
    public Community convert(CommunityDto source) {
        Community target;
        if (source.getId() == null) {
            target = new Community();
            setDefaults(target);

            Organization organization = organizationService.findById(source.getOrganizationId());
            target.setOrganizationId(source.getOrganizationId());
            target.setOrganization(organization);
            target.setOid(source.getOid());
        } else {
            target = communityService.get(source.getId());
        }

        convert(source, target);
        return target;
    }

    @Override
    public void convert(CommunityDto source, Community entity) {
        entity.setName(source.getName());
        entity.setEmail(source.getEmail());
        entity.setPhone(source.getPhone());
        entity.setLastModified(Instant.now());
        entity.setIsSharingData(source.getIsSharingData());
        entity.setNumberOfBeds(source.getNumberOfBeds());
        entity.setNumberOfVacantBeds(source.getNumberOfVacantBeds());
        entity.setLicenseNumber(source.getLicenseNumber());
        entity.setWebsiteUrl(source.getWebsiteUrl());

        if (marketplaceCommunitySecurityService.canConfigure(entity.getId())) {
            entity.setReceiveNonNetworkReferrals(source.getAllowExternalInboundReferrals());
        } else if (entity.getId() != null) {
            entity.setReceiveNonNetworkReferrals(DEFAULT_RECEIVE_NON_NETWORK_REFERRALS);
        }

        fillAddress(source, entity);
        fillDocutrackConfig(source, entity);
    }

    private void fillAddress(CommunityDto source, Community entity) {
        var foundAddress = fetchCommunityAddress(entity);

        if (foundAddress.isPresent()) {
            communityAddressEntityConverter.convert(source, foundAddress.get());
        } else {
            var communityAddress = communityAddressEntityConverter.convert(source);
            communityAddress.setCommunity(entity);
            communityAddress.setOrganization(entity.getOrganization());
            communityAddress.setOrganizationId(entity.getOrganizationId());
            putAddress(entity, communityAddress);
        }
    }


    private Optional<CommunityAddress> fetchCommunityAddress(Community entity) {
        return Optional.ofNullable(entity.getAddresses())
                .filter(CollectionUtils::isNotEmpty)
                .map(communityAddresses -> communityAddresses.get(0));
    }

    private void putAddress(Community entity, CommunityAddress communityAddress) {
        if (entity.getAddresses() == null) {
            entity.setAddresses(new ArrayList<>());
        }
        entity.getAddresses().add(communityAddress);
    }

    private void setDefaults(Community target) {
        target.setCreatedAutomatically(Boolean.FALSE);
        target.setInactive(Boolean.FALSE);
        target.setModuleCloudStorage(Boolean.FALSE);
        target.setModuleHie(Boolean.TRUE);
        target.setTestingTraining(Boolean.FALSE);
        target.setLegacyId(UUID.randomUUID().toString());
        target.setLegacyTable(CARE_COORDINATION_LEGACY_TABLE);
        target.setIsSharingData(Boolean.FALSE);
    }

    private void fillDocutrackConfig(CommunityDto source, Community entity) {
        if (docutrackSecurityService.canConfigureDocutrackInOrg(entity.getOrganizationId())
                && source.getDocutrackPharmacyConfig() != null) {

            var config = source.getDocutrackPharmacyConfig();

            if (Boolean.TRUE.equals(entity.getIsDocutrackPharmacy()) || config.getIsIntegrationEnabled()) {
                entity.setIsDocutrackPharmacy(config.getIsIntegrationEnabled());
                entity.setDocutrackClientType(config.getClientType());
                entity.setDocutrackServerDomain(config.getServerDomain());

                if (entity.getBusinessUnitCodes() == null) {
                    entity.setBusinessUnitCodes(config.getBusinessUnitCodes());
                } else {
                    entity.getBusinessUnitCodes().clear();
                    if (CollectionUtils.isNotEmpty(config.getBusinessUnitCodes())) {
                        entity.getBusinessUnitCodes().addAll(config.getBusinessUnitCodes());
                    }
                }
            }
        }
    }
}
