package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ConsanaPractitionerDto {

    private List<ConsanaIdentifierDto> identifier;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ConsanaHumanNameDto name;

    private List<ConsanaPractitionerRoleDto> practitionerRole;

    public List<ConsanaIdentifierDto> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<ConsanaIdentifierDto> identifier) {
        this.identifier = identifier;
    }

    public ConsanaHumanNameDto getName() {
        return name;
    }

    public void setName(ConsanaHumanNameDto name) {
        this.name = name;
    }

    public List<ConsanaPractitionerRoleDto> getPractitionerRole() {
        return practitionerRole;
    }

    public void setPractitionerRole(List<ConsanaPractitionerRoleDto> practitionerRole) {
        this.practitionerRole = practitionerRole;
    }
}
