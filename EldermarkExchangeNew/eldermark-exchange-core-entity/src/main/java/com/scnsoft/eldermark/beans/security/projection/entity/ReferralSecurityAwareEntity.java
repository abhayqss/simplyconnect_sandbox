package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public interface ReferralSecurityAwareEntity extends ClientIdAware {

    Long getRequestingEmployeeId();

    Long getRequestingCommunityId();

}
