package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.utils.NameUtils;

public interface AuthorIdNamesAware {

    Long getAuthorId();

    String getAuthorFirstName();

    String getAuthorLastName();

    default String getAuthorFullName() {
        return NameUtils.getFullName(getAuthorFirstName(), getAuthorLastName());
    }
}
