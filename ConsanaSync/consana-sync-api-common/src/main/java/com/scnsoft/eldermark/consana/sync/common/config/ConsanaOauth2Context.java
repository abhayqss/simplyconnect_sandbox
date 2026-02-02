package com.scnsoft.eldermark.consana.sync.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "consana.auth.oauth2")
public class ConsanaOauth2Context {

    private String url;
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String scopes;
    private String subjectRole;
    private String subjectId;
    private String organizationId;
    private String organization;
    private String purposeOfUse;
    private String npi;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getSubjectRole() {
        return subjectRole;
    }

    public void setSubjectRole(String subjectRole) {
        this.subjectRole = subjectRole;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPurposeOfUse() {
        return purposeOfUse;
    }

    public void setPurposeOfUse(String purposeOfUse) {
        this.purposeOfUse = purposeOfUse;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    @Override
    public String toString() {
        return "ConsanaOauth2Context{" +
                "url='" + url + '\'' +
                ", grantType='" + grantType + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", scopes='" + scopes + '\'' +
                ", subjectRole='" + subjectRole + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", organization='" + organization + '\'' +
                ", purposeOfUse='" + purposeOfUse + '\'' +
                ", npi='" + npi + '\'' +
                '}';
    }
}
