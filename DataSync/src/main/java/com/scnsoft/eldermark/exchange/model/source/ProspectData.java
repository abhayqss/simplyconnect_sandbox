package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(ProspectData.TABLE_NAME)
public class ProspectData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Prospect";
    public static final String PROSPECT_ID = "Prospect_ID";

    @Id
    @Column(PROSPECT_ID)
    private long id;

    @Column("Date_Became_Prospect")
    private Date dateBecameProspect;

    @Column("First_Name")
    private String firstName;

    @Column("Last_Name")
    private String lastName;

    @Column("Reserve_Unit_Number")
    private String reserveUnitNumber;

    @Column("Move_In_Date")
    private Date moveInDate;

    @Column("Move_Out_Date")
    private Date moveOutDate;

    @Column("Deposit_Date")
    private Date depositDate;

    @Column("Assessment_Date")
    private Date assessmentDate;

    @Column("SalesRep_Employee_ID")
    private String salesRepEmployeeId;

    @Column("Unit_is_Reserved")
    private String unitIsReserved;

    @Column("Reserved_From")
    private Date reservedFrom;

    @Column("Reserved_To")
    private Date reservedTo;

    @Column("Reserve_Facility")
    private String reserveFacility;

    @Column("Current_Status")
    private String currentStatus;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Facility_Primary")
    private String facilityPrimary;

    @Column("Referral_Source_Prof_Cont_ID")
    private Long referralSourceProfContId;

    @Column("Second_Occupant")
    private Boolean secondOccupant;

    @Column("Resident_Status")
    private String residentStatus;

    @Column("Resident_Facility")
    private String residentFacility;

    @Column("Resident_Unit")
    private String residentUnit;

    @Column("Copied_From_Inquiry_ID")
    private Long copiedFromInquiryId;

    @Column("Cancel_Date")
    private Date cancelDate;

    @Column("Related_Party_First_Name")
    private String relatedPartyFirstName;

    @Column("Related_Party_Last_Name")
    private String relatedPartyLastName;

    @Column("Related_Party_Phones")
    private String relatedPartyPhones;

    @Column("Pros_Phones")
    private String prosPhones;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateBecameProspect() {
        return dateBecameProspect;
    }

    public void setDateBecameProspect(Date dateBecameProspect) {
        this.dateBecameProspect = dateBecameProspect;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getReserveUnitNumber() {
        return reserveUnitNumber;
    }

    public void setReserveUnitNumber(String reserveUnitNumber) {
        this.reserveUnitNumber = reserveUnitNumber;
    }

    public Date getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(Date moveInDate) {
        this.moveInDate = moveInDate;
    }

    public Date getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(Date moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public Date getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getSalesRepEmployeeId() {
        return salesRepEmployeeId;
    }

    public void setSalesRepEmployeeId(String salesRepEmployeeId) {
        this.salesRepEmployeeId = salesRepEmployeeId;
    }

    public String getUnitIsReserved() {
        return unitIsReserved;
    }

    public void setUnitIsReserved(String unitIsReserved) {
        this.unitIsReserved = unitIsReserved;
    }

    public Date getReservedFrom() {
        return reservedFrom;
    }

    public void setReservedFrom(Date reservedFrom) {
        this.reservedFrom = reservedFrom;
    }

    public Date getReservedTo() {
        return reservedTo;
    }

    public void setReservedTo(Date reservedTo) {
        this.reservedTo = reservedTo;
    }

    public String getReserveFacility() {
        return reserveFacility;
    }

    public void setReserveFacility(String reserveFacility) {
        this.reserveFacility = reserveFacility;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getFacilityPrimary() {
        return facilityPrimary;
    }

    public void setFacilityPrimary(String facilityPrimary) {
        this.facilityPrimary = facilityPrimary;
    }

    public Long getReferralSourceProfContId() {
        return referralSourceProfContId;
    }

    public void setReferralSourceProfContId(Long referralSourceProfContId) {
        this.referralSourceProfContId = referralSourceProfContId;
    }

    public Boolean getSecondOccupant() {
        return secondOccupant;
    }

    public void setSecondOccupant(Boolean secondOccupant) {
        this.secondOccupant = secondOccupant;
    }

    public String getResidentStatus() {
        return residentStatus;
    }

    public void setResidentStatus(String residentStatus) {
        this.residentStatus = residentStatus;
    }

    public String getResidentFacility() {
        return residentFacility;
    }

    public void setResidentFacility(String residentFacility) {
        this.residentFacility = residentFacility;
    }

    public String getResidentUnit() {
        return residentUnit;
    }

    public void setResidentUnit(String residentUnit) {
        this.residentUnit = residentUnit;
    }

    public Long getCopiedFromInquiryId() {
        return copiedFromInquiryId;
    }

    public void setCopiedFromInquiryId(Long copiedFromInquiryId) {
        this.copiedFromInquiryId = copiedFromInquiryId;
    }

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    public String getRelatedPartyFirstName() {
        return relatedPartyFirstName;
    }

    public void setRelatedPartyFirstName(String relatedPartyFirstName) {
        this.relatedPartyFirstName = relatedPartyFirstName;
    }

    public String getRelatedPartyLastName() {
        return relatedPartyLastName;
    }

    public void setRelatedPartyLastName(String relatedPartyLastName) {
        this.relatedPartyLastName = relatedPartyLastName;
    }

    public String getRelatedPartyPhones() {
        return relatedPartyPhones;
    }

    public void setRelatedPartyPhones(String relatedPartyPhones) {
        this.relatedPartyPhones = relatedPartyPhones;
    }

    public String getProsPhones() {
        return prosPhones;
    }

    public void setProsPhones(String prosPhones) {
        this.prosPhones = prosPhones;
    }

}
