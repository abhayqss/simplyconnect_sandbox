package com.scnsoft.eldermark.service.validation;

import com.scnsoft.eldermark.entity.phr.AccessRight;

import java.util.Collection;

public interface AccessibleResidentsProvider {
    Collection<Long> getAccessibleResidentsOrThrow(Long authorityId);

    Collection<Long> getAccessibleResidentsOrThrow(Long authorityId, AccessRight.Code accessRightCode);

    Long getMainResidentOrThrow(Long authorityId);

    Long getMainResidentOrThrow(Long authorityId, AccessRight.Code accessRightCode);
}
