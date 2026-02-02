package com.scnsoft.eldermark.beans.projection;

public interface SignatureRequestTemplateNameEmployeeIdAware extends ClientIdClientOrganizationIdClientCommunityIdAware, ClientNamesAware, IdAware {
    Long getRequestedById();
}