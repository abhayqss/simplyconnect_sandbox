package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeStatusAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;

public interface EmployeeSecurityAwareEntity extends IdAware, OrganizationIdAware, CommunityIdAware, EmployeeStatusAware {
    Long getCareTeamRoleId();
    Long getCreatorId();
}
