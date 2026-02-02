package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.utils.NameUtils;

public interface EmployeeNamesAware {

    String getEmployeeFirstName();

    String getEmployeeLastName();

    default String getEmployeeFullName() {
        return NameUtils.getFullName(getEmployeeFirstName(), getEmployeeLastName());
    }
}
