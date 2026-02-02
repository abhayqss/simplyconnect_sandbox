package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistoryAction;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;

import java.time.Instant;

public interface DocumentSignatureHistoryCommentsAware {

    DocumentSignatureHistoryAction getAction();

    Instant getDate();

    Instant getRequestDateExpires();

    SignatureRequestNotificationMethod getRequestNotificationMethod();

    String getRequestPhoneNumber();

    String getRequestEmail();

    String getRequestPdcflowErrorMessage();

    static DocumentSignatureHistoryCommentsAware fromEntity(DocumentSignatureHistory history) {
        return new DocumentSignatureHistoryCommentsAware() {
            @Override
            public DocumentSignatureHistoryAction getAction() {
                return history.getAction();
            }

            @Override
            public Instant getDate() {
                return history.getDate();
            }

            @Override
            public Instant getRequestDateExpires() {
                return history.getRequest().getDateExpires();
            }

            @Override
            public SignatureRequestNotificationMethod getRequestNotificationMethod() {
                return history.getRequest().getNotificationMethod();
            }

            @Override
            public String getRequestPhoneNumber() {
                return history.getRequest().getPhoneNumber();
            }

            @Override
            public String getRequestEmail() {
                return history.getRequest().getEmail();
            }

            @Override
            public String getRequestPdcflowErrorMessage() {
                return history.getRequest().getPdcflowErrorMessage();
            }
        };
    }
}
