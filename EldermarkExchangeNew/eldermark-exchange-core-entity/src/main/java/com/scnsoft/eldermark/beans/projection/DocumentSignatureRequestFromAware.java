package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;

public interface DocumentSignatureRequestFromAware {
    Long getRequestedById();
    Long getRequestedFromClientId();
    Long getRequestedFromEmployeeId();
    Long getClientId();
    SignatureRequestNotificationMethod getNotificationMethod();
}
