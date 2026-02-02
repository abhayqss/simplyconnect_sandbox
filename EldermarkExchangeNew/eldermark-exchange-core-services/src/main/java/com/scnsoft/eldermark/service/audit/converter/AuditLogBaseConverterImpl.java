package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.projection.SignatureRequestTemplateNameAware;
import com.scnsoft.eldermark.util.CareCoordinationUtils;

public abstract class AuditLogBaseConverterImpl {

    protected String resolveNameAndRole(SignatureRequestTemplateNameAware aware) {
        return aware.getRequestedFromClientId() != null
                ? CareCoordinationUtils.getFullName(aware.getRequestedFromClientFirstName(), aware.getRequestedFromClientLastName())
                : CareCoordinationUtils.getFullName(aware.getRequestedFromEmployeeFirstName(), aware.getRequestedFromEmployeeLastName()) + " " + aware.getRequestedFromEmployeeCareTeamRoleName();
    }
}