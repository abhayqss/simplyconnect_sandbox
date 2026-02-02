package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.utils.NameUtils;

public interface ClientNamesAware {

    String getClientFirstName();

    String getClientLastName();

    default String getClientFullName() {
        return NameUtils.getFullName(getClientFirstName(), getClientLastName());
    }
}
