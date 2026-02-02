package com.scnsoft.eldermark.beans.projection;

import org.springframework.util.CollectionUtils;

import java.util.List;

public interface AssociatedEmployeeIdsAware extends AssociatedEmployeeIdAware {

    List<Long> getAssociatedEmployeeIds();

    @Override
    default Long getAssociatedEmployeeId() {
        return CollectionUtils.isEmpty(getAssociatedEmployeeIds()) ? null : getAssociatedEmployeeIds().get(0);
    }
}
