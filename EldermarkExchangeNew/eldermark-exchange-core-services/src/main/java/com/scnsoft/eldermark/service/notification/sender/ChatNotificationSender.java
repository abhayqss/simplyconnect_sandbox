package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;

public interface ChatNotificationSender {
    boolean sendDocumentSignatureRequestNotificationAndWait(DocumentSignatureRequestNotification signatureRequestNotification);
}
