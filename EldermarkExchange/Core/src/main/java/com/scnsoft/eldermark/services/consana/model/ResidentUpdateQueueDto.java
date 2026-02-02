package com.scnsoft.eldermark.services.consana.model;

import java.util.Objects;
import java.util.Set;

public class ResidentUpdateQueueDto {

    private Long residentId;
    private Set<ResidentUpdateType> updateTypes;
    private Long updateTime;

    public ResidentUpdateQueueDto() {
    }

    public ResidentUpdateQueueDto(Long residentId, Set<ResidentUpdateType> updateTypes, Long updateTime) {
        this.residentId = residentId;
        this.updateTypes = updateTypes;
        this.updateTime = updateTime;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Set<ResidentUpdateType> getUpdateTypes() {
        return updateTypes;
    }

    public void setUpdateTypes(Set<ResidentUpdateType> updateTypes) {
        this.updateTypes = updateTypes;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ResidentUpdateDto{" +
                "residentId=" + residentId +
                ", updateTypes=" + updateTypes +
                ", updateTime=" + updateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResidentUpdateQueueDto that = (ResidentUpdateQueueDto) o;
        return Objects.equals(residentId, that.residentId) &&
                Objects.equals(updateTypes, that.updateTypes) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(residentId, updateTypes, updateTime);
    }
}
