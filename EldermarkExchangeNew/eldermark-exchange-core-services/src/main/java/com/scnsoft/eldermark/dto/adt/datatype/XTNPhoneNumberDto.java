package com.scnsoft.eldermark.dto.adt.datatype;

public class XTNPhoneNumberDto {

    //if renaming of any field is needed - please make sure to do the same changes to #displayXTN in eventNotificationSecureEmail.vm

    private String telephoneNumber;
    private String telecommunicationUseCode;
    private String telecommunicationEquipmentType;
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

    public String getTelecommunicationUseCode() {
        return telecommunicationUseCode;
    }

    public void setTelecommunicationUseCode(String telecommunicationUseCode) {
        this.telecommunicationUseCode = telecommunicationUseCode;
    }

    public String getTelecommunicationEquipmentType() {
        return telecommunicationEquipmentType;
    }

    public void setTelecommunicationEquipmentType(String telecommunicationEquipmentType) {
        this.telecommunicationEquipmentType = telecommunicationEquipmentType;
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

}
