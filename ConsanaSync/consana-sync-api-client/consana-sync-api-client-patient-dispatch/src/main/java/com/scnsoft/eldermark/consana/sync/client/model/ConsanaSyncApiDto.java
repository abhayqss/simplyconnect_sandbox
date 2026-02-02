package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateType;

import java.util.Objects;

public class ConsanaSyncApiDto {

    @JsonProperty(value = "PatientId")
    private String patientId;

    @JsonProperty(value = "OrganizationId")
    private String organizationId;

    @JsonProperty(value = "CommunityId")
    private String communityId;

    @JsonProperty(value = "UpdateType")
    private ConsanaPatientUpdateType updateType;

    public ConsanaSyncApiDto() {
    }


    public ConsanaSyncApiDto(String patientId, String organizationId, String communityId, ConsanaPatientUpdateType updateType) {
        this.patientId = patientId;
        this.organizationId = organizationId;
        this.communityId = communityId;
        this.updateType = updateType;
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

    @Override
    public String toString() {
        return "ConsanaSyncApiDto{" +
                "patientId='" + patientId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", communityId='" + communityId + '\'' +
                ", updateType=" + updateType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsanaSyncApiDto that = (ConsanaSyncApiDto) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(communityId, that.communityId) &&
                updateType == that.updateType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, organizationId, communityId, updateType);
    }
}
