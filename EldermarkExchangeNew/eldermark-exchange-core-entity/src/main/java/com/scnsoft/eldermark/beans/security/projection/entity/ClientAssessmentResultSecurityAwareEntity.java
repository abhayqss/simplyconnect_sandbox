package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAssessmentResultSecurityFieldsAware;

public interface ClientAssessmentResultSecurityAwareEntity extends IdAware, EmployeeIdAware,
        ClientAssessmentResultSecurityFieldsAware {
}
