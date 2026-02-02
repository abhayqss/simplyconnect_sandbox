package com.scnsoft.scansol.shared;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Date: 14.05.15
 * Time: 10:00
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolBaseEmployeeInfoDto {
    private Long id;
    private String name;
    private String lastName;
    private String firstName;
    private String login;
    private String inactive;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getInactive() {
        return inactive;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }
}
