package com.scnsoft.eldermark.service.document.signature.notification;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;

import java.time.Instant;
import java.util.Optional;

public interface SignatureNotificationService {

    void sendRequestSignatureNotification(DocumentSignatureRequest documentSignatureRequest);

    void sendRequestSignaturePinCodeSmsNotification(DocumentSignatureRequest documentSignatureRequest);

    Optional<Instant> getCanResendPinTimeAt(DocumentSignatureRequest request);

    Instant getCanResendPinTimeAt(DocumentSignatureRequestNotification previousNotification);

    DocumentSignatureRequestNotification resendPin(DocumentSignatureRequest request);
}
