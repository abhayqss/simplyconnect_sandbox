package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.ServicePlanSecurityFieldsAware;

public interface ServicePlanSecurityService {

    boolean canAdd(ServicePlanSecurityFieldsAware dto);

    boolean canEdit(Long servicePlanId);

    boolean canViewList();

    boolean canView(Long servicePlanId);

    boolean canViewByClientId(Long clientId);
}
