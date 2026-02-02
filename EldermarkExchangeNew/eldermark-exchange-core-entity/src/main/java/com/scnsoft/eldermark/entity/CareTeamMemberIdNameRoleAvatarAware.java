package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.beans.projection.CareTeamMemberIdNameAware;

public interface CareTeamMemberIdNameRoleAvatarAware extends CareTeamMemberIdNameAware {
    Long getEmployeeAvatarId();
    String getCareTeamRoleName();
}
