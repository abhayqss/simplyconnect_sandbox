package com.scnsoft.eldermark.dto.pointclickcare.model.adt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccADTRecordDetails {
    private String accessingEntityId;
    private String actionCode;
    private String actionType;
    private String additionalBedDesc;
    private Long additionalBedId;
    private String additionalFloorDesc;
    private Long additionalFloorId;
    private String additionalRoomDesc;
    private Long additionalRoomId;
    private String additionalUnitDesc;
    private Long additionalUnitId;
    private String admissionSource;
    private String admissionSourceCode;
    private String admissionType;
    private String admissionTypeCode;
    private Long adtRecordId;
    private String bedDesc;
    private Long bedId;
    private String destination;
    private String destinationType;
    private String dischargeStatus;
    private String dischargeStatusCode;
    private Instant effectiveDateTime;
    private String enteredBy;
    private Long enteredByPositionId;
    private Instant enteredDate;
    private String floorDesc;
    private Long floorId;
    private Instant modifiedDateTime;
    private String origin;
    private String originType;
    private boolean outpatient;
    private String outpatientStatus; //'active' or 'inactive'
    private Long patientId;
    private String payerCode;
    private String payerName;

    //managedCare
    //medicaid
    //medicareA
    //medicareB
    //medicareD
    //other
    //outpatient
    //private
    private String payerType;

    private boolean qhsWaiver;
    private String roomDesc;
    private Long roomId;
    private boolean skilledCare;
    private LocalDate skilledEffectiveFromDate;
    private LocalDate skilledEffectiveToDate;

    //Admission
    //Death
    //Discharge
    //Internal Transfer
    //Leave
    //Return from Leave
    //Room Reserve
    private String standardActionType;
    private LocalDate stopBillingDate;
    private String unitDesc;
    private Long unitId;
    private boolean isCancelledRecord;


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

    public LocalDate getSkilledEffectiveFromDate() {
        return skilledEffectiveFromDate;
    }

    public void setSkilledEffectiveFromDate(LocalDate skilledEffectiveFromDate) {
        this.skilledEffectiveFromDate = skilledEffectiveFromDate;
    }

    public LocalDate getSkilledEffectiveToDate() {
        return skilledEffectiveToDate;
    }

    public void setSkilledEffectiveToDate(LocalDate skilledEffectiveToDate) {
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

    public boolean getIsCancelledRecord() {
        return isCancelledRecord;
    }

    public void setCancelledRecord(boolean cancelledRecord) {
        isCancelledRecord = cancelledRecord;
    }
}
