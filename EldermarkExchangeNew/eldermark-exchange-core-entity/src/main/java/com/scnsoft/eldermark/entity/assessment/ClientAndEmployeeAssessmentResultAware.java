package com.scnsoft.eldermark.entity.assessment;

import com.scnsoft.eldermark.utils.NameUtils;

public interface ClientAndEmployeeAssessmentResultAware {
    Long getClientId();
    String getClientFirstName();
    String getClientLastName();
    String getClientCommunityName();
    Long getEmployeeId();
    String getEmployeeFirstName();
    String getEmployeeLastName();

    default String getClientFullName() {
        return NameUtils.getFullName(getClientFirstName(), getClientLastName());
    }

    default String getEmployeeFullName() {
        return NameUtils.getFullName(getEmployeeFirstName(), getEmployeeLastName());
    }
}
