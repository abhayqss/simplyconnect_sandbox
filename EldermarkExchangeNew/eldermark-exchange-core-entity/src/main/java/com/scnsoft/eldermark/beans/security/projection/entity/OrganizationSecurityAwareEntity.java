package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.IdAware;

public interface OrganizationSecurityAwareEntity extends IdAware {
    boolean getLabsEnabled();
    boolean isSdohReportsEnabled();
}
