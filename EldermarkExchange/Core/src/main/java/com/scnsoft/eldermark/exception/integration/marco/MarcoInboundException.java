package com.scnsoft.eldermark.exception.integration.marco;

public class MarcoInboundException extends RuntimeException {
    private static final long serialVersionUID = 1503622994834085418L;

    private MarcoUnassignedReason unassignedReason;

    public MarcoInboundException(MarcoUnassignedReason unassignedReason) {
        super(unassignedReason.message());
        this.unassignedReason = unassignedReason;
    }

    public MarcoInboundException(Throwable cause) {
        super(cause);
        this.unassignedReason = MarcoUnassignedReason.INTERNAL_ERROR;
    }

    public MarcoInboundException() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public MarcoUnassignedReason getUnassignedReason() {
        return unassignedReason;
    }
    public void setUnassignedReason(MarcoUnassignedReason unassignedReason) {
        this.unassignedReason = unassignedReason;
    }
}
