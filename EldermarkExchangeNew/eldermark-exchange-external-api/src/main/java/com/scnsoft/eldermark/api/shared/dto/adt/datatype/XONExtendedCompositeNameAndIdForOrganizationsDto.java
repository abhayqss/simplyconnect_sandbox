package com.scnsoft.eldermark.api.shared.dto.adt.datatype;

public class XONExtendedCompositeNameAndIdForOrganizationsDto {
    private String idNumber;
    private String organizationName;
    private String organizationNameTypeCode;
    private HDHierarchicDesignatorDto assigningAuthority;
    private HDHierarchicDesignatorDto assigningFacility;
    private String identifierTypeCode;
    private String nameRepresentationCode;

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationNameTypeCode() {
        return organizationNameTypeCode;
    }

    public void setOrganizationNameTypeCode(String organizationNameTypeCode) {
        this.organizationNameTypeCode = organizationNameTypeCode;
    }

    public HDHierarchicDesignatorDto getAssigningAuthority() {
        return assigningAuthority;
    }

    public void setAssigningAuthority(HDHierarchicDesignatorDto assigningAuthority) {
        this.assigningAuthority = assigningAuthority;
    }

    public HDHierarchicDesignatorDto getAssigningFacility() {
        return assigningFacility;
    }

    public void setAssigningFacility(HDHierarchicDesignatorDto assigningFacility) {
        this.assigningFacility = assigningFacility;
    }

    public String getIdentifierTypeCode() {
        return identifierTypeCode;
    }

    public void setIdentifierTypeCode(String identifierTypeCode) {
        this.identifierTypeCode = identifierTypeCode;
    }

    public String getNameRepresentationCode() {
        return nameRepresentationCode;
    }

    public void setNameRepresentationCode(String nameRepresentationCode) {
        this.nameRepresentationCode = nameRepresentationCode;
    }
}
