package com.scnsoft.eldermark.entity.signature;

import java.util.EnumSet;
import java.util.Set;

public enum DocumentSignatureHistoryAction {

    DOCUMENT_SIGNATURE_REQUESTED,
    DOCUMENT_SIGNATURE_REQUEST_CANCELED,
    DOCUMENT_SIGNATURE_FAILED,
    DOCUMENT_SIGNED,
    DOCUMENT_SIGNATURE_REQUEST_EXPIRED,
    DOCUMENT_SENT,
    DOCUMENT_RECEIVED;

    private static final Set<DocumentSignatureHistoryAction> signatureRequestSentActions =
            EnumSet.of(DOCUMENT_SIGNATURE_REQUESTED, DOCUMENT_SENT);

    private static final Set<DocumentSignatureHistoryAction> signatureRequestCompletedActions =
            EnumSet.of(DOCUMENT_SIGNED, DOCUMENT_RECEIVED);

    public static Set<DocumentSignatureHistoryAction> signatureRequestCompletedActions() {
        return signatureRequestCompletedActions;
    }

    public boolean isSignatureRequestSentAction() {
        return signatureRequestSentActions.contains(this);
    }

    public boolean isSignatureRequestCompletedAction() {
        return signatureRequestCompletedActions.contains(this);
    }
}
