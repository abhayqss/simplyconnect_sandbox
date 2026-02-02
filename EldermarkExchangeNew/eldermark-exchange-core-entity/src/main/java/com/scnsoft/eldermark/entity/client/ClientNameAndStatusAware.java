package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.IdNamesAware;

public interface ClientNameAndStatusAware extends IdNamesAware {
    Boolean getActive();
}
