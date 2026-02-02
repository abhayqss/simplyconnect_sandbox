package com.scnsoft.eldermark.entity.client.report;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.entity.Person;

import java.time.Instant;

public class ClientDetailsOutreachReportItem implements NamesAware {
    private Long id;
    private String firstName;
    private String lastName;
    private String medicareNumber;
    private ClientDeactivationReason deactivationReason;
    private Instant intakeDate;
    private Instant exitDate;
    private Long communityId;
    private String communityName;
    private Long organizationId;

    public ClientDetailsOutreachReportItem(
            Long id,
            String firstName,
            String lastName,
            String medicareNumber,
            ClientDeactivationReason deactivationReason,
            Instant intakeDate,
            Instant exitDate,
            Long communityId,
            String communityName,
            Long organizationId
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.medicareNumber = medicareNumber;
        this.deactivationReason = deactivationReason;
        this.intakeDate = intakeDate;
        this.exitDate = exitDate;
        this.communityId = communityId;
        this.communityName = communityName;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public ClientDeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(ClientDeactivationReason deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Instant getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Instant intakeDate) {
        this.intakeDate = intakeDate;
    }

    public Instant getExitDate() {
        return exitDate;
    }

    public void setExitDate(Instant exitDate) {
        this.exitDate = exitDate;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
