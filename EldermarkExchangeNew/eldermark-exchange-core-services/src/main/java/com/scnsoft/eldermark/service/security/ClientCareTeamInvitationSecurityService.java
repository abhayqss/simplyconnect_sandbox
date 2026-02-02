package com.scnsoft.eldermark.service.security;

public interface ClientCareTeamInvitationSecurityService {

    boolean canViewList();

    boolean canView(Long invitationId);

    boolean canInvite(Long clientId);

    boolean canResend(Long invitationId);

    boolean canCancel(Long invitationId);

    boolean canAcceptOrDecline(Long invitationId);

    boolean existsInbound(Long clientId);

    boolean existsAccessibleToTargetEmployee(Long employeeId);
}
