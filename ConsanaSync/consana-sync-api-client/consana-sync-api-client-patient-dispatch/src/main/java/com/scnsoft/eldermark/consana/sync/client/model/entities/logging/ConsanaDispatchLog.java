package com.scnsoft.eldermark.consana.sync.client.model.entities.logging;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
public class ConsanaDispatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "process_datetime")
    private Instant processDatetime;

    @Column(name = "consana_patient_id")
    private String consanaPatientId;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "community_id")
    private String communityId;

    @Column(name = "is_success")
    private boolean success;

    @Column(name = "error_message")
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getProcessDatetime() {
        return processDatetime;
    }

    public void setProcessDatetime(Instant processDatetime) {
        this.processDatetime = processDatetime;
    }

    public String getConsanaPatientId() {
        return consanaPatientId;
    }

    public void setConsanaPatientId(String consanaPatientId) {
        this.consanaPatientId = consanaPatientId;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
