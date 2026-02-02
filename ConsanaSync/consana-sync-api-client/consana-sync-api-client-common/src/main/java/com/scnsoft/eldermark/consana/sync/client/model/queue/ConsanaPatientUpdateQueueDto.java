package com.scnsoft.eldermark.consana.sync.client.model.queue;

import java.util.Objects;

public class ConsanaPatientUpdateQueueDto {

    private String patientId;
    private String organizationId;
    private String communityId;
    private ConsanaPatientUpdateType updateType;
    private Long updateTime;

    public ConsanaPatientUpdateQueueDto() {
    }

    public ConsanaPatientUpdateQueueDto(String patientId, String organizationId, String communityId, ConsanaPatientUpdateType updateType, Long updateTime) {
        this.patientId = patientId;
        this.organizationId = organizationId;
        this.communityId = communityId;
        this.updateType = updateType;
        this.updateTime = updateTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public ConsanaPatientUpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(ConsanaPatientUpdateType updateType) {
        this.updateType = updateType;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ConsanaPatientUpdateQueueDto{" +
                "patientId='" + patientId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", communityId='" + communityId + '\'' +
                ", updateType=" + updateType +
                ", updateTime=" + updateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsanaPatientUpdateQueueDto that = (ConsanaPatientUpdateQueueDto) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(communityId, that.communityId) &&
                updateType == that.updateType &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, organizationId, communityId, updateType, updateTime);
    }
}
