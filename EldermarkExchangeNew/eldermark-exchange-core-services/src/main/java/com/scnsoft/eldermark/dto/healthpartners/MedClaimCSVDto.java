package com.scnsoft.eldermark.dto.healthpartners;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;

import java.time.LocalDate;

public class MedClaimCSVDto implements HpCsvRecord {

    private static final String DATE_PATTERN = "yyyyMMdd";

    @CsvBindByName(column = "MEMBER_IDENTIFIER")
    private String memberIdentifier;

    @CsvBindByName(column = "MEMBER_FIRST_NM")
    private String memberFirstName;

    @CsvBindByName(column = "MEMBER_MIDDLE_NM")
    private String memberMiddleName;

    @CsvBindByName(column = "MEMBER_LAST_NM")
    private String memberLastName;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "MEMBER_BIRTH_DT")
    private LocalDate dateOfBirth;

    @CsvBindByName(column = "CLAIM_NO")
    private String claimNo;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "SERVICE_START_DT")
    private LocalDate serviceDate;

    @CsvBindByName(column = "ICD_CODE_SET")
    private Integer icdVersion;

    @CsvBindByName(column = "DIAGNOSIS_CD")
    private String diagnosisCode;

    @CsvBindByName(column = "DIAGNOSIS_TXT")
    private String diagnosisTxt;

    @CsvBindByName(column = "PHYSICIAN_FIRST_NM")
    private String physicianFirstName;

    @CsvBindByName(column = "PHYSICIAN_MIDDLE_NM")
    private String physicianMiddleName;

    @CsvBindByName(column = "PHYSICIAN_LAST_NM")
    private String physicianLastName;

    @CsvIgnore
    private int lineNumber;

    @Override
    public String getMemberIdentifier() {
        return memberIdentifier;
    }

    public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }

    @Override
    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    @Override
    public String getMemberMiddleName() {
        return memberMiddleName;
    }

    public void setMemberMiddleName(String memberMiddleName) {
        this.memberMiddleName = memberMiddleName;
    }

    @Override
    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    @Override
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Integer getIcdVersion() {
        return icdVersion;
    }

    public void setIcdVersion(Integer icdVersion) {
        this.icdVersion = icdVersion;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisTxt() {
        return diagnosisTxt;
    }

    public void setDiagnosisTxt(String diagnosisTxt) {
        this.diagnosisTxt = diagnosisTxt;
    }

    public String getPhysicianFirstName() {
        return physicianFirstName;
    }

    public void setPhysicianFirstName(String physicianFirstName) {
        this.physicianFirstName = physicianFirstName;
    }

    public String getPhysicianMiddleName() {
        return physicianMiddleName;
    }

    public void setPhysicianMiddleName(String physicianMiddleName) {
        this.physicianMiddleName = physicianMiddleName;
    }

    public String getPhysicianLastName() {
        return physicianLastName;
    }

    public void setPhysicianLastName(String physicianLastName) {
        this.physicianLastName = physicianLastName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
