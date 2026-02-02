package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;

public interface CareTeamSecurityFieldsAware extends ClientIdAware, EmployeeIdAware, CommunityIdAware {

    Long getCareTeamRoleId();

}
