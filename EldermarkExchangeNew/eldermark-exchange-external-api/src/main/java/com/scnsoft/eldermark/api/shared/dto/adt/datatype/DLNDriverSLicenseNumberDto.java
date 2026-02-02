package com.scnsoft.eldermark.api.shared.dto.adt.datatype;

import java.util.Date;

public class DLNDriverSLicenseNumberDto {

    private String licenseNumber;
    private String issuingStateProvinceCountry;
    private Date expirationDate;

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getIssuingStateProvinceCountry() {
        return issuingStateProvinceCountry;
    }

    public void setIssuingStateProvinceCountry(String issuingStateProvinceCountry) {
        this.issuingStateProvinceCountry = issuingStateProvinceCountry;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
