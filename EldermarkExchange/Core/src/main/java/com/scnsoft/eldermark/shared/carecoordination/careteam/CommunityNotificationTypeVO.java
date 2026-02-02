package com.scnsoft.eldermark.shared.carecoordination.careteam;

public class CommunityNotificationTypeVO extends EntityEventTypeVO {
    private Long communityId;
    private Long eventTypeId;

    public CommunityNotificationTypeVO(Long communityId, Long eventTypeId) {
        this.communityId = communityId;
        this.eventTypeId = eventTypeId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    @Override
    public Long getEntityId() {
        return getCommunityId();
    }

    @Override
    public Long getEventTypeId() {
        return eventTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommunityNotificationTypeVO that = (CommunityNotificationTypeVO) o;

        if (communityId != null ? !communityId.equals(that.communityId) : that.communityId != null) return false;
        return eventTypeId != null ? eventTypeId.equals(that.eventTypeId) : that.eventTypeId == null;
    }

    @Override
    public int hashCode() {
        int result = communityId != null ? communityId.hashCode() : 0;
        result = 31 * result + (eventTypeId != null ? eventTypeId.hashCode() : 0);
        return result;
    }
}
