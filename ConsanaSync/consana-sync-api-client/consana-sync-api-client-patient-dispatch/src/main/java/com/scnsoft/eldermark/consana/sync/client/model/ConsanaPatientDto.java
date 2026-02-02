package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ConsanaPatientDto {

    private List<ConsanaIdentifierDto> identifier;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;

    private List<ConsanaHumanNameDto> name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ConsanaContactPointDto> telecom;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gender;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String birthDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ConsanaAddressDto> address;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ConsanaCodeableConceptDto maritalStatus;

    public List<ConsanaIdentifierDto> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<ConsanaIdentifierDto> identifier) {
        this.identifier = identifier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ConsanaHumanNameDto> getName() {
        return name;
    }

    public void setName(List<ConsanaHumanNameDto> name) {
        this.name = name;
    }

    public List<ConsanaContactPointDto> getTelecom() {
        return telecom;
    }

    public void setTelecom(List<ConsanaContactPointDto> telecom) {
        this.telecom = telecom;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<ConsanaAddressDto> getAddress() {
        return address;
    }

    public void setAddress(List<ConsanaAddressDto> address) {
        this.address = address;
    }

    public ConsanaCodeableConceptDto getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(ConsanaCodeableConceptDto maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}
