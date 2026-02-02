package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DLN_Driver_s_License_Number")
public class DLNDriverSLicenseNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "issuing_spc")
    private String issuingStateProvinceCountry;

    @Column(name = "expiration_date")
    private Date expirationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
