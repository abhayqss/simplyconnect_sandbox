package com.scnsoft.eldermark.dto.healthpartners;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RxClaimCSVDto implements HpCsvRecord {

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

    @CsvBindByName(column = "DAYS_SUPPLY")
    private Integer daysSupply;

    @CsvBindByName(column = "PRESCRIBER_FIRST_NM")
    private String prescriberFirstName;

    @CsvBindByName(column = "PRESCRIBER_MIDDLE_NM")
    private String prescriberMiddleName;

    @CsvBindByName(column = "PRESCRIBER_LAST_NM")
    private String prescriberLastName;

    @CsvBindByName(column = "PRESCRIBER_NPI")
    private String prescribingPhysicianNPI;

    @CsvBindByName(column = "COMPOUND_CD")
    private String compoundCode;

    @CsvBindByName(column = "DAW_CD")
    private String DAWProductSelectionCode;

    @CsvBindByName(column = "REFILL_NO")
    private Integer refillNumber;

    @CsvBindByName(column = "PRESCRIPTION_ORIGIN_CD")
    private String prescriptionOriginCode;

    @CsvBindByName(column = "DRUG_NM")
    private String drugName;

    @CsvBindByName(column = "BRAND_GENERIC_CD")
    private String planReportedBrandGenericCode;

    @CsvBindByName(column = "NATIONAL_DRUG_CODE")
    private String nationalDrugCode;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "FILLED_DT")
    private LocalDate serviceDate;

    @CsvBindByName(column = "PRESCRIPTION_NO")
    private String RXNumber;

    @CsvBindByName(column = "CLAIM_ADJUSTED_FROM_IDENTIFIER")
    private String claimAdjustedFromIdentifier;

    @CsvBindByName(column = "RELATED_CLAIM_RELATIONSHIP")
    private String relatedClaimRelationship;

    @CsvBindByName(column = "QUANTITY_DISPENSED")
    private BigDecimal quantityDispensed;

    @CsvBindByName(column = "UNIT_OF_MEASURE")
    private String quantityQualifierCode;

    @CsvBindByName(column = "PHARMACY_NM")
    private String pharmacyName;

    @CsvBindByName(column = "PHARMACY_NABP")
    private String claimBillingProvider;

    @CsvBindByName(column = "PHARMACY_NPI")
    private String pharmacyNPI;

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

    public Integer getDaysSupply() {
        return daysSupply;
    }

    public void setDaysSupply(Integer daysSupply) {
        this.daysSupply = daysSupply;
    }

    public String getPrescriberFirstName() {
        return prescriberFirstName;
    }

    public void setPrescriberFirstName(String prescriberFirstName) {
        this.prescriberFirstName = prescriberFirstName;
    }

    public String getPrescriberMiddleName() {
        return prescriberMiddleName;
    }

    public void setPrescriberMiddleName(String prescriberMiddleName) {
        this.prescriberMiddleName = prescriberMiddleName;
    }

    public String getPrescriberLastName() {
        return prescriberLastName;
    }

    public void setPrescriberLastName(String prescriberLastName) {
        this.prescriberLastName = prescriberLastName;
    }

    public String getPrescribingPhysicianNPI() {
        return prescribingPhysicianNPI;
    }

    public void setPrescribingPhysicianNPI(String prescribingPhysicianNPI) {
        this.prescribingPhysicianNPI = prescribingPhysicianNPI;
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    public String getDAWProductSelectionCode() {
        return DAWProductSelectionCode;
    }

    public void setDAWProductSelectionCode(String DAWProductSelectionCode) {
        this.DAWProductSelectionCode = DAWProductSelectionCode;
    }

    public Integer getRefillNumber() {
        return refillNumber;
    }

    public void setRefillNumber(Integer refillNumber) {
        this.refillNumber = refillNumber;
    }

    public String getPrescriptionOriginCode() {
        return prescriptionOriginCode;
    }

    public void setPrescriptionOriginCode(String prescriptionOriginCode) {
        this.prescriptionOriginCode = prescriptionOriginCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getPlanReportedBrandGenericCode() {
        return planReportedBrandGenericCode;
    }

    public void setPlanReportedBrandGenericCode(String planReportedBrandGenericCode) {
        this.planReportedBrandGenericCode = planReportedBrandGenericCode;
    }

    public String getNationalDrugCode() {
        return nationalDrugCode;
    }

    public void setNationalDrugCode(String nationalDrugCode) {
        this.nationalDrugCode = nationalDrugCode;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getRXNumber() {
        return RXNumber;
    }

    public void setRXNumber(String RXNumber) {
        this.RXNumber = RXNumber;
    }

    public String getClaimAdjustedFromIdentifier() {
        return claimAdjustedFromIdentifier;
    }

    public void setClaimAdjustedFromIdentifier(String claimAdjustedFromIdentifier) {
        this.claimAdjustedFromIdentifier = claimAdjustedFromIdentifier;
    }

    public String getRelatedClaimRelationship() {
        return relatedClaimRelationship;
    }

    public void setRelatedClaimRelationship(String relatedClaimRelationship) {
        this.relatedClaimRelationship = relatedClaimRelationship;
    }

    public BigDecimal getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(BigDecimal quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
    }

    public String getQuantityQualifierCode() {
        return quantityQualifierCode;
    }

    public void setQuantityQualifierCode(String quantityQualifierCode) {
        this.quantityQualifierCode = quantityQualifierCode;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getClaimBillingProvider() {
        return claimBillingProvider;
    }

    public void setClaimBillingProvider(String claimBillingProvider) {
        this.claimBillingProvider = claimBillingProvider;
    }

    public String getPharmacyNPI() {
        return pharmacyNPI;
    }

    public void setPharmacyNPI(String pharmacyNPI) {
        this.pharmacyNPI = pharmacyNPI;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
