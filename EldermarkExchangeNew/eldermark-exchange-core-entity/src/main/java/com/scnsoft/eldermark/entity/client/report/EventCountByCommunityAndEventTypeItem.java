package com.scnsoft.eldermark.entity.client.report;

public class EventCountByCommunityAndEventTypeItem {

    private String eventTypeCode;
    private Long communityId;
    private Long count;

    public EventCountByCommunityAndEventTypeItem(String eventTypeCode, Long communityId, Long count) {
        this.eventTypeCode = eventTypeCode;
        this.communityId = communityId;
        this.count = count;
    }

    public String getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(String eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
