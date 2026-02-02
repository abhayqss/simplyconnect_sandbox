package com.scnsoft.eldermark.dto.security;

import javax.validation.constraints.NotEmpty;

public class LoginSso4dDto {
    @NotEmpty
    private String companyId;
    @NotEmpty
    private String subdomain;
    @NotEmpty
    private String port;
    @NotEmpty
    private String userId;
    @NotEmpty
    private String sessionId;
    private String resNum;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getResNum() {
        return resNum;
    }

    public void setResNum(String resNum) {
        this.resNum = resNum;
    }
}
