package com.scnsoft.eldermark.mobile.projection.client;

import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.projection.IsFavouriteEvaluatedAware;

public interface MobileClientListInfo extends
        IdAware,
        CommunityIdAware,
        CommunityNameAware,
        OrganizationIdNameAware,
        AvatarIdNameAware,
        IsFavouriteEvaluatedAware {

    String getFirstName();

    String getLastName();

    String getMiddleName();

    Long getAssociatedEmployeeId();

    String getAssociatedEmployeeTwilioUserSid();
}
