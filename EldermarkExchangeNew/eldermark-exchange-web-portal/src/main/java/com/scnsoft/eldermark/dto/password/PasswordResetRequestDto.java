package com.scnsoft.eldermark.dto.password;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordResetRequestDto {

    @NotNull
    private String email;
    
    @NotNull
    @JsonProperty(value = "companyId")
    private String companyCode; 

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    
}
