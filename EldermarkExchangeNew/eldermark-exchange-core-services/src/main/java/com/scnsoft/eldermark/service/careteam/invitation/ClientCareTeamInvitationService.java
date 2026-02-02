package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.beans.ClientCareTeamInvitationFilter;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationAcceptDeclineValidationFieldsAware;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationStatusAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitation;
import com.scnsoft.eldermark.entity.careteam.invitation.ConfirmInviteCareTeamMemberData;
import com.scnsoft.eldermark.entity.careteam.invitation.InviteCareTeamMemberData;
import com.scnsoft.eldermark.exception.CareTeamInvitationClientHieConsentValidationException;
import com.scnsoft.eldermark.service.ProjectingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ClientCareTeamInvitationService extends ProjectingService<Long> {

    <P> Page<P> find(ClientCareTeamInvitationFilter filter, PermissionFilter permissionFilter,
                     Class<P> projectionClass,
                     Pageable pageable);

    Long count(ClientCareTeamInvitationFilter filter, PermissionFilter permissionFilter);

    void validateHieConsent(Long clientId) throws CareTeamInvitationClientHieConsentValidationException;

    ClientCareTeamInvitation invite(InviteCareTeamMemberData invitationData);

    boolean canResend(Long invitationId);

    boolean canResend(ClientCareTeamInvitationStatusAware invitation);

    boolean canCancel(Long invitationId);

    boolean canCancel(ClientCareTeamInvitationStatusAware invitation);

    boolean canAcceptOrDecline(Long invitationId);

    boolean canAcceptOrDecline(ClientCareTeamInvitationAcceptDeclineValidationFieldsAware invitation);

    void decline(Long invitationId);

    void accept(Long invitationId);

    void confirmInvitation(ConfirmInviteCareTeamMemberData confirmInvitationData);

    ClientCareTeamInvitation resendInvitation(Long id, InviteCareTeamMemberData data);

    void cancelInvitation(Long id);

    void expireInvitations();

    String resolveRedirectCreateAccountFromEmail(String token);

    boolean existsInbound(Long clientId, Set<Long> targetEmployeeIds);

    boolean existsAccessibleToTargetEmployee(Long employeeId, PermissionFilter permissionFilter);

    boolean existsIncomingForHieConsentChange(Long clientId);

    <T> List<T> findIncomingForHieConsentChangeInCommunity(Set<Long> affectedClientIds, String organizationAlternativeId,
                                                           Class<T> projectionClass);

    void cancelInvitationsForOptOutAsync(Long clientId, String organizationAlternativeId);

    void cancelInvitationsForOptOutInCommunityAsync(Set<Long> affectedClientIds, String organizationAlternativeId);

}
