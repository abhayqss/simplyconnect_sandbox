package com.scnsoft.eldermark.beans.security.projection;

import com.scnsoft.eldermark.entity.basic.Telecom;

public interface PersonTelecomDataAware extends Telecom {
    Long getPersonEmployeeId();
    String getPersonEmployeeFirstName();
    String getPersonEmployeeLastName();
}
