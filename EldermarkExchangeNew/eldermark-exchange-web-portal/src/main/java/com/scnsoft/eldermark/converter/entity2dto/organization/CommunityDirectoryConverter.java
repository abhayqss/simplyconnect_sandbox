package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.projection.IdNameOrganizationIdAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAwareImpl;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.directory.DirCommunityListItemDto;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.service.CommunityHieConsentPolicyService;
import com.scnsoft.eldermark.service.security.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CommunityDirectoryConverter implements ListAndItemConverter<IdNameOrganizationIdAware, DirCommunityListItemDto> {

    @Autowired
    private ContactSecurityService contactSecurityService;

    @Autowired
    private CommunitySecurityService communitySecurityService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private NoteSecurityService noteSecurityService;

    @Autowired
    private ReferralSecurityService referralSecurityService;

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Override
    public <E extends IdNameOrganizationIdAware> List<DirCommunityListItemDto> convertList(List<E> sourceList) {

        var communityIds = CareCoordinationUtils.toIdsSet(sourceList);

        var policyMap = communityHieConsentPolicyService.findAllByCommunityIdsAndArchived(
                        communityIds,
                        false,
                        HieConsentPolicyTypeAware.class
                )
                .stream()
                .collect(Collectors.toMap(HieConsentPolicyTypeAware::getCommunityId, HieConsentPolicyTypeAware::getType));

        return sourceList.stream()
                .map(it -> convert(it, policyMap.get(it.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public DirCommunityListItemDto convert(IdNameOrganizationIdAware community) {

        var policy = communityHieConsentPolicyService.findByCommunityIdAndArchived(community.getId(), false)
                .map(CommunityHieConsentPolicy::getType)
                .orElse(null);

        return convert(community, policy);
    }

    private DirCommunityListItemDto convert(IdNameOrganizationIdAware community, HieConsentPolicyType consentPolicyType) {
        DirCommunityListItemDto target = new DirCommunityListItemDto();
        target.setId(community.getId());
        target.setName(community.getName());
        target.setCanAddContact(contactSecurityService.canAddAnyRole(community.getOrganizationId(), community.getId()));
        target.setCanViewOrHasAccessibleClient(communitySecurityService.canView(community.getId()) || communitySecurityService.hasAccessibleClient(community.getId()));
        target.setCanAddClient(canAddClient(community));
        target.setCanAddGroupNote(canAddGroupNote(community));
        target.setCanViewInboundReferrals(referralSecurityService.canViewInboundsInCommunity(community.getId()));
        target.setCanViewOutboundReferrals(referralSecurityService.canViewOutboundsInCommunity(community.getId()));

        if (consentPolicyType != null) {
            target.setHieConsentPolicyName(consentPolicyType);
            target.setHieConsentPolicyTitle(consentPolicyType.getDisplayName());
        }

        return target;
    }

    private boolean canAddClient(IdNameOrganizationIdAware community) {
        return clientSecurityService.canAdd(new ClientSecurityFieldsAwareImpl(community.getOrganizationId(), community.getId()));
    }

    private boolean canAddGroupNote(IdNameOrganizationIdAware community) {
        return noteSecurityService.canAddGroupNoteToCommunity(community.getId());
    }

    private interface HieConsentPolicyTypeAware {
        Long getCommunityId();

        HieConsentPolicyType getType();
    }
}
