package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.OrganizationAffiliationInfoItemDto;
import com.scnsoft.eldermark.dto.OrganizationBaseDto;
import com.scnsoft.eldermark.dto.OrganizationFeaturesDto;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.service.AffiliatedOrganizationService;
import com.scnsoft.eldermark.service.AssessmentService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.service.security.OrganizationSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrganizationBasicDtoConverter implements ItemConverter<Organization, OrganizationBaseDto> {

    @Autowired
    private StateService stateService;

    @Autowired
    private OrganizationSecurityService organizationSecurityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private AffiliatedOrganizationService affiliatedOrganizationService;

    @Autowired
    private AffiliatedInfoConverter affiliatedInfoConverter;

    @Autowired
    private AssessmentService assessmentService;

    @Override
    public void convert(Organization source, OrganizationBaseDto target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setOid(source.getOid());
        target.setLogoName(source.getMainLogoPath());
        String stateAbbr = null;
        //target.setOrgLogo(organizationService.convertFileIntoBytes(source.getMainLogoPath()));
        if (source.getAddressAndContacts() != null) {
            target.setZipCode(source.getAddressAndContacts().getPostalCode());
            target.setCity(source.getAddressAndContacts().getCity());
            target.setStreet(source.getAddressAndContacts().getStreetAddress());
            Long stateId = source.getAddressAndContacts().getStateId();
            target.setStateId(stateId);
            if (stateId != null) {
                stateAbbr = stateService.findById(stateId).map(State::getAbbr).orElse(null);
            }
            target.setPhone(source.getAddressAndContacts().getPhone());
            target.setEmail(source.getAddressAndContacts().getEmail());
        }
        if (source.getSystemSetup() != null) {
            target.setCompanyId(source.getSystemSetup().getLoginCompanyId());
        }
        if (!StringUtils.isBlank(target.getStreet()) && !StringUtils.isBlank(target.getCity())
                && !StringUtils.isBlank(stateAbbr) && !StringUtils.isBlank(target.getZipCode())) {
            target.setDisplayAddress(target.getStreet() + ", " + target.getCity() + ", " + stateAbbr + " "
                    + target.getZipCode());
        }

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        target.setHasCommunities(communityService.hasVisibleCommunities(permissionFilter, source.getId()));
        target.setAffiliationPrimary(getAffiliationPrimary(source.getId()));
        target.setAffiliationAffiliated(getAffiliationAffiliated(source.getId()));

        var features = new OrganizationFeaturesDto();
        target.setFeatures(features);
        features.setCanEdit(organizationSecurityService.canEditFeatures(source.getId()));
        features.setIsChatEnabled(source.isChatEnabled());
        features.setIsVideoEnabled(source.isVideoEnabled());
        features.setAreComprehensiveAssessmentsEnabled(assessmentService.isTypeAllowedInOrganization(Assessment.COMPREHENSIVE, source.getId()));
        features.setIsPaperlessHealthcareEnabled(BooleanUtils.isTrue(source.getIsPaperlessHealthcareEnabled()));
        features.setIsSignatureEnabled(source.isSignatureEnabled());
        features.setAreAppointmentsEnabled(source.getIsAppointmentsEnabled());

        target.setCanEdit(organizationSecurityService.canEdit(source.getId()));
        target.setCanEditAffiliateRelationships(organizationSecurityService.canConfigureAffiliateRelationships(source.getId()));
        var canConfigureMarketplace = organizationSecurityService.canConfigureMarketplace(source.getId());
        target.setCanEditAllowExternalInboundReferrals(canConfigureMarketplace);
        target.setCanEditConfirmMarketplaceVisibility(canConfigureMarketplace);
}


    @Override
    public OrganizationBaseDto convert(Organization organization) {
        OrganizationBaseDto target = new OrganizationBaseDto();
        convert(organization, target);
        return target;
    }

    private List<OrganizationAffiliationInfoItemDto> getAffiliationPrimary(Long organizationId) {
        //current organization is affiliated
        var primaryData = affiliatedOrganizationService.getAllByAffiliatedOrganizationId(organizationId);

        var filler = affiliatedInfoConverter.organizationInfoItemFiller(primaryData, AffiliatedOrganization::getAffiliatedCommunityId)
                .andThen(affiliatedInfoConverter.baseInfoItemFiller(primaryData,
                        AffiliatedOrganization::getPrimaryOrganizationId,
                        AffiliatedOrganization::getPrimaryCommunityId
                ));

        return affiliatedInfoConverter.convertAffiliationInfo(primaryData,
                AffiliatedOrganization::getPrimaryOrganizationId,
                OrganizationAffiliationInfoItemDto::new,
                filler);
    }

    private List<OrganizationAffiliationInfoItemDto> getAffiliationAffiliated(Long organizationId) {
        //current organization is primary
        var affiliatedData = affiliatedOrganizationService.getAllByPrimaryOrganizationId(organizationId);

        var filler = affiliatedInfoConverter.organizationInfoItemFiller(affiliatedData, AffiliatedOrganization::getPrimaryCommunityId)
                .andThen(affiliatedInfoConverter.baseInfoItemFiller(affiliatedData,
                        AffiliatedOrganization::getAffiliatedOrganizationId,
                        AffiliatedOrganization::getAffiliatedCommunityId
                ));

        return affiliatedInfoConverter.convertAffiliationInfo(affiliatedData,
                AffiliatedOrganization::getAffiliatedOrganizationId,
                OrganizationAffiliationInfoItemDto::new,
                filler
        );
    }
}
