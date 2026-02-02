package com.scnsoft.eldermark.shared.carecoordination.careteam;

public class ResidentNotificationEventTypeVO extends EntityEventTypeVO {
    private Long residentId;
    private Long eventTypeId;

    public ResidentNotificationEventTypeVO(Long residentId, Long eventTypeId) {
        this.residentId = residentId;
        this.eventTypeId = eventTypeId;
    }

    public Long getResidentId() {
        return residentId;
    }

    @Override
    public Long getEntityId() {
        return getResidentId();
    }

    @Override
    public Long getEventTypeId() {
        return eventTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResidentNotificationEventTypeVO that = (ResidentNotificationEventTypeVO) o;

        if (residentId != null ? !residentId.equals(that.residentId) : that.residentId != null) return false;
        return eventTypeId != null ? eventTypeId.equals(that.eventTypeId) : that.eventTypeId == null;
    }

    @Override
    public int hashCode() {
        int result = residentId != null ? residentId.hashCode() : 0;
        result = 31 * result + (eventTypeId != null ? eventTypeId.hashCode() : 0);
        return result;
    }
}
