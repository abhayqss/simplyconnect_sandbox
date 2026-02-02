package com.scnsoft.eldermark.entity.client.report;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.Person;

import java.time.Instant;
import java.time.LocalDate;

public class ClientDetailsItem {
    private Long id;
    private String firstName;
    private String lastName;
    private Long communityId;
    private String communityName;
    private ClientDeactivationReason deactivationReason;
    private Instant deactivationDate;
    private Long organizationId;
    private String organizationName;
    private Boolean active;
    private String medicaidNumber;
    private Person person;
    private LocalDate birthDate;
    private String inNetworkInsuranceDisplayName;

    public ClientDetailsItem(
            Long id,
            String firstName,
            String lastName,
            Long communityId,
            String communityName,
            ClientDeactivationReason deactivationReason,
            Instant deactivationDate,
            Long organizationId,
            String organizationName,
            Boolean active,
            String medicaidNumber,
            Person person,
            String inNetworkInsuranceDisplayName,
            LocalDate birthDate
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.communityId = communityId;
        this.communityName = communityName;
        this.deactivationReason = deactivationReason;
        this.deactivationDate = deactivationDate;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.active = active;
        this.medicaidNumber = medicaidNumber;
        this.person = person;
        this.birthDate = birthDate;
        this.inNetworkInsuranceDisplayName = inNetworkInsuranceDisplayName;
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

    public ClientDeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(ClientDeactivationReason deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Instant getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Instant deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getInNetworkInsuranceDisplayName() {
        return inNetworkInsuranceDisplayName;
    }

    public void setInNetworkInsuranceDisplayName(String inNetworkInsuranceDisplayName) {
        this.inNetworkInsuranceDisplayName = inNetworkInsuranceDisplayName;
    }
}