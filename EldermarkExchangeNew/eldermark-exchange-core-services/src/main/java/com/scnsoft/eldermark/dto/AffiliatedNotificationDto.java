package com.scnsoft.eldermark.dto;

import java.util.Objects;

public class AffiliatedNotificationDto {

    private long primaryOrganizationId;
    private long affiliatedOrganizationId;
    private boolean isTerminated;

    public AffiliatedNotificationDto(long primaryOrganizationId, long affiliatedOrganizationId, boolean isTerminated) {
        this.primaryOrganizationId = primaryOrganizationId;
        this.affiliatedOrganizationId = affiliatedOrganizationId;
        this.isTerminated = isTerminated;
    }

    public long getPrimaryOrganizationId() {
        return primaryOrganizationId;
    }

    public void setPrimaryOrganizationId(long primaryOrganizationId) {
        this.primaryOrganizationId = primaryOrganizationId;
    }

    public long getAffiliatedOrganizationId() {
        return affiliatedOrganizationId;
    }

    public void setAffiliatedOrganizationId(long affiliatedOrganizationId) {
        this.affiliatedOrganizationId = affiliatedOrganizationId;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AffiliatedNotificationDto that = (AffiliatedNotificationDto) o;
        return primaryOrganizationId == that.primaryOrganizationId
            && affiliatedOrganizationId == that.affiliatedOrganizationId
            && isTerminated == that.isTerminated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryOrganizationId, affiliatedOrganizationId, isTerminated);
    }
}
