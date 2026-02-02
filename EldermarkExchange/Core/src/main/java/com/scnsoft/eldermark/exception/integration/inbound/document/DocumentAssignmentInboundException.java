package com.scnsoft.eldermark.exception.integration.inbound.document;

public class DocumentAssignmentInboundException extends RuntimeException {
    private static final long serialVersionUID = 1503622994834085418L;

    private DocumentAssignmentErrorType unassignedReason;

    public DocumentAssignmentInboundException(DocumentAssignmentErrorType unassignedReason) {
        super(unassignedReason.message());
        this.unassignedReason = unassignedReason;
    }

    public DocumentAssignmentInboundException(Throwable cause) {
        super(cause);
        this.unassignedReason = DocumentAssignmentErrorType.INTERNAL_ERROR;
    }

    public DocumentAssignmentInboundException() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public DocumentAssignmentErrorType getUnassignedReason() {
        return unassignedReason;
    }
    public void setUnassignedReason(DocumentAssignmentErrorType unassignedReason) {
        this.unassignedReason = unassignedReason;
    }
}
