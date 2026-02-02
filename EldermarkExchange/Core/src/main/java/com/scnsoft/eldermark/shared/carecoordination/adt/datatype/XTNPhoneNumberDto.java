package com.scnsoft.eldermark.shared.carecoordination.adt.datatype;

import org.apache.commons.lang.StringUtils;

public class XTNPhoneNumberDto {
    private String telephoneNumber;
    private String phoneNumber;
    private String countryCode;
    private String areaCode;
    private String extension;
    private String email;
    private String anyText;

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnyText() {
        return anyText;
    }

    public void setAnyText(String anyText) {
        this.anyText = anyText;
    }

    @Override
    public String toString(){
        StringBuilder phone = new StringBuilder();
        if(StringUtils.isNotEmpty(countryCode)){
            phone.append(countryCode).append("-");
        }
        if(StringUtils.isNotEmpty(countryCode)){
            phone.append(areaCode).append("-");
        }
        if(StringUtils.isNotEmpty(countryCode)){
            phone.append(phoneNumber).append("-");
        }

        return phone.toString();
    }
}
