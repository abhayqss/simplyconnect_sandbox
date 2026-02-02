package com.scnsoft.eldermark.projection;

import com.scnsoft.eldermark.beans.projection.AvatarIdNameAware;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.CommunityNameAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdNameAware;
import com.scnsoft.eldermark.entity.IdNamesBirthDateAware;

public interface EmployeeIdNameFavouriteOrgDetails extends
        IdNamesBirthDateAware,
        CommunityIdAware,
        CommunityNameAware,
        OrganizationIdNameAware,
        AvatarIdNameAware,
        IsFavouriteEvaluatedAware
{

    String getCareTeamRoleName();

    String getLoginName();

    String getTwilioUserSid();
}
