package com.scnsoft.eldermark.mobile.dto;

import javax.validation.constraints.NotEmpty;

public class LoginDto {

    @NotEmpty
    private String companyCode;
    @NotEmpty
    private String login;
    @NotEmpty
    private String password;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
