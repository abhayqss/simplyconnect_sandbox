package com.scnsoft.eldermark.util.document.singature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;
import com.scnsoft.eldermark.entity.signature.SignatureRequestRecipientType;

import java.time.Instant;
import java.util.Objects;

public final class DocumentSignatureRequestUtils {

    private DocumentSignatureRequestUtils() {
    }

    public static SignatureRequestRecipientType resolveRecipientType(DocumentSignatureRequest request) {
        if (request.getRequestedFromClientId() != null) {
            return SignatureRequestRecipientType.CLIENT;
        } else {
            return Objects.equals(request.getRequestedById(), request.getRequestedFromEmployeeId())
                    ? SignatureRequestRecipientType.SELF
                    : SignatureRequestRecipientType.STAFF;
        }
    }

    public static DocumentSignatureRequestStatus resolveCorrectRequestStatus(DocumentSignatureRequest request) {
        var status = request.getStatus();
        return status.isSignatureRequestSentStatus() && request.getDateExpires().isBefore(Instant.now())
                ? DocumentSignatureRequestStatus.EXPIRED
                : status;
    }


    public static DocumentSignatureStatus resolveCorrectSignatureStatus(DocumentSignatureRequest request) {
        return DocumentSignatureStatus.fromRequestStatus(resolveCorrectRequestStatus(request));
    }
}
