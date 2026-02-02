package com.scnsoft.eldermark.beans;

public class EventGroupStatistics {

    private String groupName;
    private Long count;   

    public EventGroupStatistics(String groupName, Long count) {
        this.groupName = groupName;
        this.count = count;       
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
