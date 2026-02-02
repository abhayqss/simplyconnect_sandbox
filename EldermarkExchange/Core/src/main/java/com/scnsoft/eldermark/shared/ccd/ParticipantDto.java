package com.scnsoft.eldermark.shared.ccd;

public class ParticipantDto {
    private String timeLow;

    private String timeHigh;

    private String roleCode;

    private String relationship;

    private PersonDto person;

    private OrganizationDto organization;

    public String getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(String timeLow) {
        this.timeLow = timeLow;
    }

    public String getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(String timeHigh) {
        this.timeHigh = timeHigh;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

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
