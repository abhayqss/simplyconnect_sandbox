package com.scnsoft.eldermark.shared;

import java.io.Serializable;

public class EmployeeDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Long databaseId;
    private String login;
    private String alternativeDatabaseId;

    private boolean isEldermarkUser;
    private boolean isManager;
    private boolean isDirectManager;

    private String logoUrl;
    private String alternativeLogoUrl;

    private String roleDisplayName;

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

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        this.isManager = manager;
    }

    public boolean isEldermarkUser() {
        return isEldermarkUser;
    }

    public void setEldermarkUser(boolean eldermarkUser) {
        isEldermarkUser = eldermarkUser;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public boolean isDirectManager() {
        return isDirectManager;
    }

    public void setDirectManager(boolean directManager) {
        isDirectManager = directManager;
    }

    public String getFullName(){
        if ((firstName!=null) && (lastName!=null)) {
            return firstName + " " + lastName;
        } else if (firstName!=null) {
            return firstName;
        } else if (lastName!=null) {
            return lastName;
        } else if (login!=null) {
            return login;
        } else {
            return "";
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setAlternativeDatabaseId(String alternativeDatabaseId) {
        this.alternativeDatabaseId = alternativeDatabaseId;
    }

    public String getAlternativeDatabaseId() {
        return alternativeDatabaseId;
    }

    public String getAlternativeLogoUrl() {
        return alternativeLogoUrl;
    }

    public void setAlternativeLogoUrl(String alternativeLogoUrl) {
        this.alternativeLogoUrl = alternativeLogoUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleDisplayName() {
        return roleDisplayName;
    }

    public void setRoleDisplayName(String roleDisplayName) {
        this.roleDisplayName = roleDisplayName;
    }
}
