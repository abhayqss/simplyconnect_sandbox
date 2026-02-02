package com.scnsoft.eldermark.shared.carecoordination.service;

/**
 * Created by pzhurba on 09-Nov-15.
 */
public class ResetPasswordRequestDto {
    private String email;

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
