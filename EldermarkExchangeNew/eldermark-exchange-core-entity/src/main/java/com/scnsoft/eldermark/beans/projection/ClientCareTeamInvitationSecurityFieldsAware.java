package com.scnsoft.eldermark.beans.projection;

public interface ClientCareTeamInvitationSecurityFieldsAware extends
        ClientIdAware,
        ClientCommunityIdAware,
        ClientOrganizationIdAware,
        ClientCareTeamInvitationAcceptDeclineValidationFieldsAware {
    Long getTargetEmployeeId();

}
