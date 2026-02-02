package com.scnsoft.eldermark.entity.pointclickcare;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "PccAdtRecord")
public class PccAdtRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "saved_at", nullable = false)
    private Instant savedAt;

    @Column(name = "accessing_entity_id")
    private String accessingEntityId;

    @Column(name = "action_code")
    private String actionCode;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "additional_bed_desc")
    private String additionalBedDesc;

    @Column(name = "additional_bed_id")
    private Long additionalBedId;

    @Column(name = "additional_floor_desc")
    private String additionalFloorDesc;

    @Column(name = "additional_floor_id")
    private Long additionalFloorId;

    @Column(name = "additional_room_desc")
    private String additionalRoomDesc;

    @Column(name = "additional_room_id")
    private Long additionalRoomId;

    @Column(name = "additional_unit_desc")
    private String additionalUnitDesc;

    @Column(name = "additional_unit_id")
    private Long additionalUnitId;

    @Column(name = "admission_source")
    private String admissionSource;

    @Column(name = "admission_source_code")
    private String admissionSourceCode;

    @Column(name = "admission_type")
    private String admissionType;

    @Column(name = "admission_type_code")
    private String admissionTypeCode;

    @Column(name = "adt_record_id")
    private Long adtRecordId;

    @Column(name = "bed_desc")
    private String bedDesc;

    @Column(name = "bed_id")
    private Long bedId;

    @Column(name = "destination")
    private String destination;

    @Column(name = "destination_type")
    private String destinationType;

    @Column(name = "discharge_status")
    private String dischargeStatus;

    @Column(name = "discharge_status_code")
    private String dischargeStatusCode;

    @Column(name = "effective_date_time")
    private Instant effectiveDateTime;

    @Column(name = "entered_by")
    private String enteredBy;

    @Column(name = "entered_by_position_id")
    private Long enteredByPositionId;

    @Column(name = "entered_date")
    private Instant enteredDate;

    @Column(name = "floor_desc")
    private String floorDesc;

    @Column(name = "floor_id")
    private Long floorId;

    @Column(name = "modified_date_time")
    private Instant modifiedDateTime;

    @Column(name = "origin")
    private String origin;

    @Column(name = "origin_type")
    private String originType;

    @Column(name = "outpatient")
    private boolean outpatient;

    @Column(name = "outpatient_status")
    private String outpatientStatus;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "payer_code")
    private String payerCode;

    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "payer_type")
    private String payerType;

    @Column(name = "qhs_waiver")
    private boolean qhsWaiver;

    @Column(name = "room_desc")
    private String roomDesc;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "skilled_care")
    private boolean skilledCare;

    @Column(name = "skilled_effective_from_date")
    private String skilledEffectiveFromDate;

    @Column(name = "skilled_effective_to_date")
    private String skilledEffectiveToDate;

    @Column(name = "standard_action_type")
    private String standardActionType;

    @Column(name = "stop_billing_date")
    private LocalDate stopBillingDate;

    @Column(name = "unit_desc")
    private String unitDesc;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "is_cancelled_record")
    private boolean isCancelledRecord;

    @Column(name = "resident_admit_date")
    private Instant clientAdmitDate;

    @Column(name = "resident_discharge_date")
    private Instant clientDischargeDate;

    @Column(name = "resident_death_date")
    private Instant clientDeathDate;

    @Column(name = "resident_deceased")
    private Boolean clientDeceased;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(Instant savedAt) {
        this.savedAt = savedAt;
    }

    public String getAccessingEntityId() {
        return accessingEntityId;
    }

    public void setAccessingEntityId(String accessingEntityId) {
        this.accessingEntityId = accessingEntityId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getAdditionalBedDesc() {
        return additionalBedDesc;
    }

    public void setAdditionalBedDesc(String additionalBedDesc) {
        this.additionalBedDesc = additionalBedDesc;
    }

    public Long getAdditionalBedId() {
        return additionalBedId;
    }

    public void setAdditionalBedId(Long additionalBedId) {
        this.additionalBedId = additionalBedId;
    }

    public String getAdditionalFloorDesc() {
        return additionalFloorDesc;
    }

    public void setAdditionalFloorDesc(String additionalFloorDesc) {
        this.additionalFloorDesc = additionalFloorDesc;
    }

    public Long getAdditionalFloorId() {
        return additionalFloorId;
    }

    public void setAdditionalFloorId(Long additionalFloorId) {
        this.additionalFloorId = additionalFloorId;
    }

    public String getAdditionalRoomDesc() {
        return additionalRoomDesc;
    }

    public void setAdditionalRoomDesc(String additionalRoomDesc) {
        this.additionalRoomDesc = additionalRoomDesc;
    }

    public Long getAdditionalRoomId() {
        return additionalRoomId;
    }

    public void setAdditionalRoomId(Long additionalRoomId) {
        this.additionalRoomId = additionalRoomId;
    }

    public String getAdditionalUnitDesc() {
        return additionalUnitDesc;
    }

    public void setAdditionalUnitDesc(String additionalUnitDesc) {
        this.additionalUnitDesc = additionalUnitDesc;
    }

    public Long getAdditionalUnitId() {
        return additionalUnitId;
    }

    public void setAdditionalUnitId(Long additionalUnitId) {
        this.additionalUnitId = additionalUnitId;
    }

    public String getAdmissionSource() {
        return admissionSource;
    }

    public void setAdmissionSource(String admissionSource) {
        this.admissionSource = admissionSource;
    }

    public String getAdmissionSourceCode() {
        return admissionSourceCode;
    }

    public void setAdmissionSourceCode(String admissionSourceCode) {
        this.admissionSourceCode = admissionSourceCode;
    }

    public String getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    public String getAdmissionTypeCode() {
        return admissionTypeCode;
    }

    public void setAdmissionTypeCode(String admissionTypeCode) {
        this.admissionTypeCode = admissionTypeCode;
    }

    public Long getAdtRecordId() {
        return adtRecordId;
    }

    public void setAdtRecordId(Long adtRecordId) {
        this.adtRecordId = adtRecordId;
    }

    public String getBedDesc() {
        return bedDesc;
    }

    public void setBedDesc(String bedDesc) {
        this.bedDesc = bedDesc;
    }

    public Long getBedId() {
        return bedId;
    }

    public void setBedId(Long bedId) {
        this.bedId = bedId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public String getDischargeStatus() {
        return dischargeStatus;
    }

    public void setDischargeStatus(String dischargeStatus) {
        this.dischargeStatus = dischargeStatus;
    }

    public String getDischargeStatusCode() {
        return dischargeStatusCode;
    }

    public void setDischargeStatusCode(String dischargeStatusCode) {
        this.dischargeStatusCode = dischargeStatusCode;
    }

    public Instant getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(Instant effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public Long getEnteredByPositionId() {
        return enteredByPositionId;
    }

    public void setEnteredByPositionId(Long enteredByPositionId) {
        this.enteredByPositionId = enteredByPositionId;
    }

    public Instant getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(Instant enteredDate) {
        this.enteredDate = enteredDate;
    }

    public String getFloorDesc() {
        return floorDesc;
    }

    public void setFloorDesc(String floorDesc) {
        this.floorDesc = floorDesc;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public Instant getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(Instant modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public boolean isOutpatient() {
        return outpatient;
    }

    public void setOutpatient(boolean outpatient) {
        this.outpatient = outpatient;
    }

    public String getOutpatientStatus() {
        return outpatientStatus;
    }

    public void setOutpatientStatus(String outpatientStatus) {
        this.outpatientStatus = outpatientStatus;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPayerCode() {
        return payerCode;
    }

    public void setPayerCode(String payerCode) {
        this.payerCode = payerCode;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerType() {
        return payerType;
    }

    public void setPayerType(String payerType) {
        this.payerType = payerType;
    }

    public boolean isQhsWaiver() {
        return qhsWaiver;
    }

    public void setQhsWaiver(boolean qhsWaiver) {
        this.qhsWaiver = qhsWaiver;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    public void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public boolean isSkilledCare() {
        return skilledCare;
    }

    public void setSkilledCare(boolean skilledCare) {
        this.skilledCare = skilledCare;
    }

    public String getSkilledEffectiveFromDate() {
        return skilledEffectiveFromDate;
    }

    public void setSkilledEffectiveFromDate(String skilledEffectiveFromDate) {
        this.skilledEffectiveFromDate = skilledEffectiveFromDate;
    }

    public String getSkilledEffectiveToDate() {
        return skilledEffectiveToDate;
    }

    public void setSkilledEffectiveToDate(String skilledEffectiveToDate) {
        this.skilledEffectiveToDate = skilledEffectiveToDate;
    }

    public String getStandardActionType() {
        return standardActionType;
    }

    public void setStandardActionType(String standardActionType) {
        this.standardActionType = standardActionType;
    }

    public LocalDate getStopBillingDate() {
        return stopBillingDate;
    }

    public void setStopBillingDate(LocalDate stopBillingDate) {
        this.stopBillingDate = stopBillingDate;
    }

    public String getUnitDesc() {
        return unitDesc;
    }

    public void setUnitDesc(String unitDesc) {
        this.unitDesc = unitDesc;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public boolean isCancelledRecord() {
        return isCancelledRecord;
    }

    public void setCancelledRecord(boolean cancelledRecord) {
        isCancelledRecord = cancelledRecord;
    }

    public Instant getClientAdmitDate() {
        return clientAdmitDate;
    }

    public void setClientAdmitDate(Instant clientAdmitDate) {
        this.clientAdmitDate = clientAdmitDate;
    }

    public Instant getClientDischargeDate() {
        return clientDischargeDate;
    }

    public void setClientDischargeDate(Instant clientDischargeDate) {
        this.clientDischargeDate = clientDischargeDate;
    }

    public Instant getClientDeathDate() {
        return clientDeathDate;
    }

    public void setClientDeathDate(Instant clientDeathDate) {
        this.clientDeathDate = clientDeathDate;
    }

    public Boolean getClientDeceased() {
        return clientDeceased;
    }

    public void setClientDeceased(Boolean clientDeceased) {
        this.clientDeceased = clientDeceased;
    }
}
