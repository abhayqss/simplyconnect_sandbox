package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.EmployeeStatus;

public interface ClientCareTeamInvitationAcceptDeclineValidationFieldsAware extends ClientCareTeamInvitationStatusAware,
        ClientCareTeamInvitationCreatedAtAware {

    EmployeeStatus getTargetEmployeeStatus();
}
