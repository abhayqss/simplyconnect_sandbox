package com.scnsoft.eldermark.entity.projection;

import com.scnsoft.eldermark.beans.projection.CareTeamRoleNameAware;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.IdNamesAware;

public interface EmployeeRoleNameStatusCommunityAware extends IdNamesAware, CareTeamRoleNameAware {

    EmployeeStatus getStatus();
    String getCommunityName();

}
