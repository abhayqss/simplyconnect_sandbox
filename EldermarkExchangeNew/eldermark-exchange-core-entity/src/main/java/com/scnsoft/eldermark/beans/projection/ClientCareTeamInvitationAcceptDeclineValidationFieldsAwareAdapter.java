package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitation;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitationStatus;

import java.time.Instant;

public class ClientCareTeamInvitationAcceptDeclineValidationFieldsAwareAdapter implements ClientCareTeamInvitationAcceptDeclineValidationFieldsAware {

    private final ClientCareTeamInvitation invitation;

    public ClientCareTeamInvitationAcceptDeclineValidationFieldsAwareAdapter(ClientCareTeamInvitation invitation) {
        this.invitation = invitation;
    }

    @Override
    public EmployeeStatus getTargetEmployeeStatus() {
        return invitation.getTargetEmployee().getStatus();
    }

    @Override
    public Instant getCreatedAt() {
        return invitation.getCreatedAt();
    }

    @Override
    public ClientCareTeamInvitationStatus getStatus() {
        return invitation.getStatus();
    }
}
