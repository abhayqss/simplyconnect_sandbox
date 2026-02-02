package com.scnsoft.scansol.shared;


import javax.validation.constraints.NotNull;

/**
 * Date: 14.05.15
 * Time: 7:54
 */
public class ScanSolEmployeeAuthRequest {
    @NotNull
    private String login;

    @NotNull
    private String companyId;

    @NotNull
    private String pwd;

    public String getLogin () {
        return login;
    }

    public void setLogin (String login) {
        this.login = login;
    }

    public String getCompanyId () {
        return companyId;
    }

    public void setCompanyId (String companyId) {
        this.companyId = companyId;
    }

    public String getPwd () {
        return pwd;
    }

    public void setPwd (String pwd) {
        this.pwd = pwd;
    }
}
