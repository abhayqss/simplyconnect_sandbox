package com.scnsoft.eldermark.entity.healthpartner;

import com.scnsoft.eldermark.entity.MedicationDispense;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "HealthPartnersRxClaim")
public class HealthPartnersRxClaim extends BaseHealthPartnersRecord {

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "is_adjustment")
    private Boolean isAdjustment;

    @Column(name = "is_duplicate")
    private Boolean isDuplicate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_dispense_id", referencedColumnName = "id")
    private MedicationDispense medicationDispense;

    @Column(name = "medication_dispense_id", insertable = false, updatable = false)
    private Long medicationDispenseId;

    @Column(name = "medication_deleted_type")
    private String medicationDeletedType;

    @Column(name = "days_supply")
    private Integer daysSupply;

    @Column(name = "prescriber_first_name")
    private String prescriberFirstName;

    @Column(name = "prescriber_middle_name")
    private String prescriberMiddleName;

    @Column(name = "prescriber_last_name")
    private String prescriberLastName;

    @Column(name = "prescribing_physician_npi")
    private String prescribingPhysicianNPI;

    @Column(name = "compound_code")
    private String compoundCode;

    @Column(name = "daw_product_selection_code")
    private String DAWProductSelectionCode;

    @Column(name = "refill_number")
    private Integer refillNumber;

    @Column(name = "prescription_origin_code")
    private String prescriptionOriginCode;

    @Column(name = "drug_name")
    private String drugName;

    @Column(name = "plan_reported_brand_generic_code")
    private String planReportedBrandGenericCode;

    @Column(name = "national_drug_code")
    private String nationalDrugCode;

    @Column(name = "service_date")
    private Instant serviceDate;

    @Column(name = "claim_no")
    private String claimNo;

    @Column(name = "rx_number")
    private String RXNumber;

    @Column(name = "claim_adjusted_from_identifier")
    private String claimAdjustedFromIdentifier;

    @Column(name = "related_claim_relationship")
    private String relatedClaimRelationship;

    @Column(name = "quantity_dispensed", columnDefinition = "decimal")
    private BigDecimal quantityDispensed;

    @Column(name = "quantity_qualifier_code")
    private String quantityQualifierCode;

    @Column(name = "pharmacy_name")
    private String pharmacyName;

    @Column(name = "claim_billing_provider")
    private String claimBillingProvider;

    @Column(name = "pharmacy_npi")
    private String pharmacyNPI;

    @Transient
    private Exception processingException;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Boolean getAdjustment() {
        return isAdjustment;
    }

    public void setAdjustment(Boolean adjustment) {
        isAdjustment = adjustment;
    }

    public Boolean getDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        isDuplicate = duplicate;
    }

    public MedicationDispense getMedicationDispense() {
        return medicationDispense;
    }

    public void setMedicationDispense(MedicationDispense medicationDispense) {
        this.medicationDispense = medicationDispense;
    }

    public Long getMedicationDispenseId() {
        return medicationDispenseId;
    }

    public void setMedicationDispenseId(Long medicationDispenseId) {
        this.medicationDispenseId = medicationDispenseId;
    }

    public String getMedicationDeletedType() {
        return medicationDeletedType;
    }

    public void setMedicationDeletedType(String medicationDeletedType) {
        this.medicationDeletedType = medicationDeletedType;
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

    public Instant getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Instant serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
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

    public Exception getProcessingException() {
        return processingException;
    }

    public void setProcessingException(Exception processingException) {
        this.processingException = processingException;
    }

    @Override
    public String toString() {
        return super.toString() + "HealthPartnersRxClaim{" +
                "lineNumber=" + lineNumber +
                ", isAdjustment=" + isAdjustment +
                ", isDuplicate=" + isDuplicate +
                ", medicationDispense=" + medicationDispense +
                ", medicationDispenseId=" + medicationDispenseId +
                ", medicationDeletedType='" + medicationDeletedType + '\'' +
                ", daysSupply=" + daysSupply +
                ", prescriberFirstName='" + prescriberFirstName + '\'' +
                ", prescriberMiddleName='" + prescriberMiddleName + '\'' +
                ", prescriberLastName='" + prescriberLastName + '\'' +
                ", prescribingPhysicianNPI='" + prescribingPhysicianNPI + '\'' +
                ", compoundCode='" + compoundCode + '\'' +
                ", DAWProductSelectionCode='" + DAWProductSelectionCode + '\'' +
                ", refillNumber=" + refillNumber +
                ", prescriptionOriginCode='" + prescriptionOriginCode + '\'' +
                ", drugName='" + drugName + '\'' +
                ", planReportedBrandGenericCode='" + planReportedBrandGenericCode + '\'' +
                ", nationalDrugCode='" + nationalDrugCode + '\'' +
                ", serviceDate=" + serviceDate +
                ", claimNo='" + claimNo + '\'' +
                ", RXNumber='" + RXNumber + '\'' +
                ", claimAdjustedFromIdentifier='" + claimAdjustedFromIdentifier + '\'' +
                ", relatedClaimRelationship='" + relatedClaimRelationship + '\'' +
                ", quantityDispensed=" + quantityDispensed +
                ", quantityQualifierCode='" + quantityQualifierCode + '\'' +
                ", pharmacyName='" + pharmacyName + '\'' +
                ", claimBillingProvider='" + claimBillingProvider + '\'' +
                ", pharmacyNPI='" + pharmacyNPI + '\'' +
                ", processingException=" + processingException +
                '}';
    }
}
