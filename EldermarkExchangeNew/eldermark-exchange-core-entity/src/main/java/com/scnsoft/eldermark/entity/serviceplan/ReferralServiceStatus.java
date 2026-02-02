package com.scnsoft.eldermark.entity.serviceplan;

public enum ReferralServiceStatus {

    PENDING("Pending", 1L),
    IN_PROCESS("In process", 2L),
    COMPLETED("Completed", 3L),
    OTHER("Other", 4L);

    private String displayName;
    private Long statusId;

    ReferralServiceStatus(String displayName, Long statusNumber) {
        this.displayName = displayName;
        this.statusId = statusNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getStatusId() {
        return statusId;
    }

    public static ReferralServiceStatus findByStatusId(Long statusId) {
        for (ReferralServiceStatus status : ReferralServiceStatus.values()) {
            if (status.statusId.equals(statusId)) {
                return status;
            }
        }
        return null;
    }
}
