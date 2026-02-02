package com.scnsoft.eldermark.service.document.signature.notification;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;

public interface SignatureCancelNotificationService {
    void sendCancelNotification(DocumentSignatureRequest request);
}
