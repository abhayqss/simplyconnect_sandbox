package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.ProspectIdAware;
import com.scnsoft.eldermark.beans.projection.SecondOccupantProspectId;

public interface AvatarSecurityAwareEntity extends ClientIdAware, EmployeeIdAware, ProspectIdAware, SecondOccupantProspectId {
}
