package com.scnsoft.eldermark.entity.signature;

import java.util.Collection;
import java.util.EnumSet;

public enum DocumentSignatureRequestStatus {

    /**
     * Signature created, but not submitted to PDCFlow yet
     */
    CREATED,

    /**
     * Signature request sent to PDCFLow
     */
    SIGNATURE_REQUESTED,

    /**
     * Signature request sent to PDCFLow for document without signature areas
     */
    REVIEW_REQUESTED,

    /**
     * Failed to submit signature request to PDCFlow
     */
    REQUEST_FAILED,

    /**
     * Signature received
     */
    SIGNED,

    /**
     * Document without signature accepted on PDCFlow
     */
    REVIEWED,

    /**
     * Received error from PDCFlow
     */
    SIGNATURE_FAILED,

    /**
     * Signature expired
     */
    EXPIRED,

    /**
     * Signature canceled
     */
    CANCELED;

    private static final Collection<DocumentSignatureRequestStatus> SIGNATURE_REQUEST_SENT_STATUSES =
            EnumSet.of(SIGNATURE_REQUESTED, REVIEW_REQUESTED);

    public boolean isSignatureRequestSentStatus() {
        return SIGNATURE_REQUEST_SENT_STATUSES.contains(this);
    }

    public static Collection<DocumentSignatureRequestStatus> signatureRequestSentStatuses() {
        return SIGNATURE_REQUEST_SENT_STATUSES;
    }
}
