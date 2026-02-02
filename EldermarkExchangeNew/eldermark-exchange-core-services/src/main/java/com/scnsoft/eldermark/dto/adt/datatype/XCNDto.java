package com.scnsoft.eldermark.dto.adt.datatype;

public class XCNDto {

    private String idNumber;
    private String lastName;
    private String firstName;
    private String middleName;
    private String degree;
    private HDHierarchicDesignatorDto assigningAuthority;
    private HDHierarchicDesignatorDto assigningFacility;

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
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
}
