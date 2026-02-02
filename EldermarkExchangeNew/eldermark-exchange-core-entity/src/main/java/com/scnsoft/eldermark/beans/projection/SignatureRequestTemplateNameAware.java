package com.scnsoft.eldermark.beans.projection;

public interface SignatureRequestTemplateNameAware extends ClientIdClientOrganizationIdClientCommunityIdAware, ClientNamesAware, IdAware {
    String getSignatureTemplateTitle();

    Long getSignatureTemplateId();

    Long getRequestedFromClientId();

    String getRequestedFromClientFirstName();

    String getRequestedFromClientLastName();

    String getRequestedFromEmployeeFirstName();

    String getRequestedFromEmployeeLastName();

    String getRequestedFromEmployeeCareTeamRoleName();
}