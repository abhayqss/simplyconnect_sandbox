package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.ClientCareTeamInvitationFilter;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInboundInvitationListItemDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationListItemDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationValidationDto;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationConfirmDto;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationDto;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationResendDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CareTeamInvitationFacade {

    Page<ClientCareTeamInvitationListItemDto> findInvitations(ClientCareTeamInvitationFilter filter, Pageable pageable);

    Page<ClientCareTeamInboundInvitationListItemDto> findInboundInvitations(Pageable pageable);

    Long countInvitations(ClientCareTeamInvitationFilter filter);

    ClientCareTeamInvitationDto findById(Long id);

    Long invite(CareTeamInvitationDto invitationDto);

    Long resendInvitation(CareTeamInvitationResendDto dto);

    boolean canInvite(Long clientId);

    void confirmRegistration(CareTeamInvitationConfirmDto inviteDto);

    void cancelInvitation(Long id);

    void acceptInvitation(Long id);

    void declineInvitation(Long id);

    boolean existsIncomingForHieConsentChange(Long clientId);

    ClientCareTeamInvitationValidationDto validateHieConsent(Long clientId);

}
