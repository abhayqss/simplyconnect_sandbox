package com.scnsoft.eldermark.beans.twilio.user;

import com.scnsoft.eldermark.beans.projection.EmployeeStatusAware;

public interface EmployeeTwilioSecurityFieldsAware extends EmployeeStatusAware {
    Boolean getOrganizationIsChatEnabled();
    Boolean getOrganizationIsVideoEnabled();
}
