package com.scnsoft.eldermark.dto.healthpartners;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.time.LocalDate;

public class TermedMembersCSVDto implements HpCsvRecord {

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

    public String getMemberIdentifier() {
        return memberIdentifier;
    }

    public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    public String getMemberMiddleName() {
        return memberMiddleName;
    }

    public void setMemberMiddleName(String memberMiddleName) {
        this.memberMiddleName = memberMiddleName;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setLineNumber(int lineNumber) {
        //do nothing
    }
}
