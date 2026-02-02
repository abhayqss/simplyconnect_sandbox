package com.scnsoft.eldermark.shared.carecoordination.contacts;


import com.scnsoft.eldermark.shared.carecoordination.NameDto;

public class LinkedContactDto extends NameDto {
    private Long id;
    private String companyId;
    private String login;
    private String role;
    private String organization;
    private String community;
    private Long databaseId;
    private String careTeamRoleCodeName;
    private Long communityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public String getCareTeamRoleCodeName() {
        return careTeamRoleCodeName;
    }

    public void setCareTeamRoleCodeName(String careTeamRoleCodeName) {
        this.careTeamRoleCodeName = careTeamRoleCodeName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }
}
