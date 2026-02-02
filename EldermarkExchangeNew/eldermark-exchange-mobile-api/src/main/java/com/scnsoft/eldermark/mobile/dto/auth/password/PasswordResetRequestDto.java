package com.scnsoft.eldermark.mobile.dto.auth.password;

import javax.validation.constraints.NotBlank;

public class PasswordResetRequestDto {

    @NotBlank
    private String companyCode;
    @NotBlank
    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    
}
