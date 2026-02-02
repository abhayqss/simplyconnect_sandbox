package com.scnsoft.eldermark.shared.carecoordination.contacts;

import com.scnsoft.eldermark.shared.carecoordination.service.ResetPasswordDto;

public class NewAccountLinkedDto extends ResetPasswordDto {
    private String login;
    private String role;
    private String organization;
    private Long databaseId;

    public NewAccountLinkedDto() {
    }

    public NewAccountLinkedDto(String token) {
        super(token);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }
}
