package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.hiepolicy.HieConsentChangeCareTeamInvitationListItemDto;

import java.util.List;

public interface ClientCareTeamInvitationFacade {

    String resolveRedirectCreateAccountFromEmail(String token);

    boolean existsIncomingForHieConsentChange(Long clientId);

    List<HieConsentChangeCareTeamInvitationListItemDto> findIncomingForHieConsentChangeInCommunity(Long communityId);

}
