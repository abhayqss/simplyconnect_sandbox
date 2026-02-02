package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.projection.HieConsentChangeCommunityInvitationProjection;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.hiepolicy.HieConsentChangeCareTeamInvitationListItemDto;
import com.scnsoft.eldermark.service.HieConsentPolicyUpdateService;
import com.scnsoft.eldermark.service.careteam.invitation.ClientCareTeamInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientCareTeamInvitationFacadeImpl implements ClientCareTeamInvitationFacade {

    @Autowired
    private ClientCareTeamInvitationService clientCareTeamInvitationService;

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Autowired
    private ListAndItemConverter<HieConsentChangeCommunityInvitationProjection, HieConsentChangeCareTeamInvitationListItemDto> hieConsentChangeCareTeamInvitationListItemDtoConverter;


    @Override
    public String resolveRedirectCreateAccountFromEmail(String token) {
        return clientCareTeamInvitationService.resolveRedirectCreateAccountFromEmail(token);
    }

    @Override
    @PreAuthorize("@clientSecurityService.canEdit(#clientId) or @clientHieConsentPolicySecurityService.canEdit(#clientId)")
    public boolean existsIncomingForHieConsentChange(@P("clientId") Long clientId) {
        return clientCareTeamInvitationService.existsIncomingForHieConsentChange(clientId);
    }

    @Override
    @PreAuthorize("@communitySecurityService.canEdit(#communityId)")
    public List<HieConsentChangeCareTeamInvitationListItemDto> findIncomingForHieConsentChangeInCommunity(@P("communityId") Long communityId) {
        return hieConsentChangeCareTeamInvitationListItemDtoConverter.convertList(
                hieConsentPolicyUpdateService.findIncomingCareTeamInvitationsForHieConsentChangeInCommunity(
                        communityId,
                        HieConsentChangeCommunityInvitationProjection.class)
        );
    }
}
