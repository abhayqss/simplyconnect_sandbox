package com.scnsoft.eldermark.consana.sync.client.model;


import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;

import java.time.Instant;
import java.util.Objects;

public class ResidentUpdateDatabaseQueueBody {

    private Long residentId;
    private ResidentUpdateType updateType;
    private Long updateTime;

    public ResidentUpdateDatabaseQueueBody() {
    }

    public ResidentUpdateDatabaseQueueBody(Long residentId, ResidentUpdateType updateType, Long updateTime) {
        this.residentId = residentId;
        this.updateType = updateType;
        this.updateTime = updateTime;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public ResidentUpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(ResidentUpdateType updateType) {
        this.updateType = updateType;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResidentUpdateDatabaseQueueBody that = (ResidentUpdateDatabaseQueueBody) o;
        return Objects.equals(residentId, that.residentId) &&
                updateType == that.updateType &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(residentId, updateType, updateTime);
    }

    @Override
    public String toString() {
        return "ResidentUpdateDatabaseQueueBody{" +
                "residentId=" + residentId +
                ", updateType=" + updateType +
                ", updateTime=" + updateTime +
                '}';
    }
}
