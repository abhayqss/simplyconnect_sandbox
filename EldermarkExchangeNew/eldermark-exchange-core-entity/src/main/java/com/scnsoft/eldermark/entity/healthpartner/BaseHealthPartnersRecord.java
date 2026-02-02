package com.scnsoft.eldermark.entity.healthpartner;

import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@MappedSuperclass
public class BaseHealthPartnersRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hp_file_log_id", nullable = false)
    private Long hpFileLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hp_file_log_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private HealthPartnersFileLog hpFileLog;

    @Column(name = "received_datetime", nullable = false)
    private Instant received;

    @Column(name = "is_success", nullable = false)
    private boolean isSuccess;

    @Column(name = "error_msg", columnDefinition = "varchar(max)")
    private String errorMessage;


    @Column(name = "member_identifier")
    private String memberIdentifier;

    @Column(name = "member_first_name")
    private String memberFirstName;

    @Column(name = "member_middle_name")
    private String memberMiddleName;

    @Column(name = "member_last_name")
    private String memberLastName;

    @Column(name = "birth_date", columnDefinition = "datetime2")
    private LocalDate birthDate;

    @Transient
    private Exception processingException;

    @Transient
    private Long clientId;

    @Transient
    private Set<ResidentUpdateType> updateTypes;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHpFileLogId() {
        return hpFileLogId;
    }

    public void setHpFileLogId(Long hpFileLogId) {
        this.hpFileLogId = hpFileLogId;
    }

    public HealthPartnersFileLog getHpFileLog() {
        return hpFileLog;
    }

    public void setHpFileLog(HealthPartnersFileLog hpFileLog) {
        this.hpFileLog = hpFileLog;
    }

    public Instant getReceived() {
        return received;
    }

    public void setReceived(Instant received) {
        this.received = received;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public String getMemberIdentifier() {
        return memberIdentifier;
    }

    public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    public String getMemberMiddleName() {
        return memberMiddleName;
    }

    public void setMemberMiddleName(String memberMiddleName) {
        this.memberMiddleName = memberMiddleName;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Exception getProcessingException() {
        return processingException;
    }

    public void setProcessingException(Exception processingException) {
        this.processingException = processingException;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Set<ResidentUpdateType> getUpdateTypes() {
        return updateTypes;
    }

    public void setUpdateTypes(Set<ResidentUpdateType> updateTypes) {
        this.updateTypes = updateTypes;
    }

    @Override
    public String toString() {
        return "BaseHealthPartnersRecord{" +
                "id=" + id +
                ", hpFileLogId=" + hpFileLogId +
                ", hpFileLog=" + hpFileLog +
                ", received=" + received +
                ", isSuccess=" + isSuccess +
                ", errorMessage='" + errorMessage + '\'' +
                ", memberIdentifier='" + memberIdentifier + '\'' +
                ", memberFirstName='" + memberFirstName + '\'' +
                ", memberMiddleName='" + memberMiddleName + '\'' +
                ", memberLastName='" + memberLastName + '\'' +
                ", birthDate=" + birthDate +
                ", processingException=" + processingException +
                '}';
    }
}
