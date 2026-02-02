package com.scnsoft.eldermark.consana.sync.client.beans;

import java.util.List;

public class ApplicationSyncContext {
    List<Long> communityIds;
    int schedule;
    boolean checkDataSyncStatus;
    boolean checkDataSyncStatusSyncFirst;

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public int getSchedule() {
        return schedule;
    }

    public void setSchedule(int schedule) {
        this.schedule = schedule;
    }

    public boolean isCheckDataSyncStatus() {
        return checkDataSyncStatus;
    }

    public void setCheckDataSyncStatus(boolean checkDataSyncStatus) {
        this.checkDataSyncStatus = checkDataSyncStatus;
    }

    public boolean isCheckDataSyncStatusSyncFirst() {
        return checkDataSyncStatusSyncFirst;
    }

    public void setCheckDataSyncStatusSyncFirst(boolean checkDataSyncStatusSyncFirst) {
        this.checkDataSyncStatusSyncFirst = checkDataSyncStatusSyncFirst;
    }
}
