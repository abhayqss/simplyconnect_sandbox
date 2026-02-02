package com.scnsoft.eldermark.beans.projection;

public interface HieConsentChangeCommunityInvitationProjection extends ClientIdAware {

    String getClientFirstName();

    String getClientLastName();

    Long getTargetEmployeeId();

    String getTargetEmployeeFirstName();

    String getTargetEmployeeLastName();
}
