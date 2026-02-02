package com.scnsoft.eldermark.entity.serviceplan;

public enum ReferralServiceRequestStatus {

    ACCEPTED("Accepted", 1L),
    DECLINED("Declined", 2L);

    private String displayName;
    private Long requestStatusId;

    ReferralServiceRequestStatus(String displayName, Long requestStatusNumber) {
        this.displayName = displayName;
        this.requestStatusId = requestStatusNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getRequestStatusId() {
        return requestStatusId;
    }

    public static ReferralServiceRequestStatus findByRequestStatusId(Long requestStatusId) {
        for (ReferralServiceRequestStatus requestStatus : ReferralServiceRequestStatus.values()) {
            if (requestStatus.requestStatusId.equals(requestStatusId)) {
                return requestStatus;
            }
        }
        return null;
    }
}
