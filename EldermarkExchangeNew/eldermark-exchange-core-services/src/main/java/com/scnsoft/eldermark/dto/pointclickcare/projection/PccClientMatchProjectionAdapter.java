package com.scnsoft.eldermark.dto.pointclickcare.projection;

import com.scnsoft.eldermark.entity.Client;

import java.time.LocalDate;

public class PccClientMatchProjectionAdapter implements PccClientMatchProjection {

    private final Client client;

    public PccClientMatchProjectionAdapter(Client client) {
        this.client = client;
    }

    @Override
    public String getOrganizationPccOrgUuid() {
        return client.getOrganization().getPccOrgUuid();
    }

    @Override
    public String getFirstName() {
        return client.getFirstName();
    }

    @Override
    public String getLastName() {
        return client.getLastName();
    }

    @Override
    public LocalDate getBirthDate() {
        return client.getBirthDate();
    }

    @Override
    public String getGenderCodeSystem() {
        return client.getGender() == null ? null : client.getGender().getCodeSystem();
    }

    @Override
    public String getGenderCode() {
        return client.getGender() == null ? null : client.getGender().getCode();
    }

    @Override
    public String getMedicaidNumber() {
        return client.getMedicaidNumber();
    }

    @Override
    public String getMedicareNumber() {
        return client.getMedicareNumber();
    }

    @Override
    public Long getPccPatientId() {
        return client.getPccPatientId();
    }

    @Override
    public Long getCommunityPccFacilityId() {
        return client.getCommunity() == null ? null : client.getCommunity().getPccFacilityId();
    }

    @Override
    public Long getId() {
        return client.getId();
    }
}
