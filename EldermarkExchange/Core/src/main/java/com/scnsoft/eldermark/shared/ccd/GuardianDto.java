package com.scnsoft.eldermark.shared.ccd;

public class GuardianDto {
    private String relationship;

    private PersonDto person;

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
}
