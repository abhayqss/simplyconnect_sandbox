package com.scnsoft.scansol.shared;

import javax.validation.constraints.NotNull;

public class ScanSolEmployeeAuthRequestForSSO {
    @NotNull
    private String login;

    @NotNull
    private String companyId;

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
}
