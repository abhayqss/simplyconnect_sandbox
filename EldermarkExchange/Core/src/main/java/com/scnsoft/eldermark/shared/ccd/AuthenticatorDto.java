package com.scnsoft.eldermark.shared.ccd;

public class AuthenticatorDto {

    private String time;

    private PersonDto person;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }
}
