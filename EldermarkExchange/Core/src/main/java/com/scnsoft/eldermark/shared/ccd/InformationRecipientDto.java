package com.scnsoft.eldermark.shared.ccd;

public class InformationRecipientDto {

    private PersonDto person;

    private OrganizationDto organization;

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public OrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDto organization) {
        this.organization = organization;
    }
}
