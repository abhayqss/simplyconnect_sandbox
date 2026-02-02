package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.model.adt.PccADTRecordDetails;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.pointclickcare.PccAdtRecordEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BiFunction;


@Component
@ConditionalOnProperty(value = "pcc.integration.enabled", havingValue = "true")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PccAdtRecordConverter implements BiFunction<PccADTRecordDetails, Client, PccAdtRecordEntity> {

    @Override
    public PccAdtRecordEntity apply(PccADTRecordDetails source, Client client) {
        var result = new PccAdtRecordEntity();

        result.setAccessingEntityId(source.getAccessingEntityId());
        result.setActionCode(source.getActionCode());
        result.setActionType(source.getActionType());
        result.setAdditionalBedDesc(source.getAdditionalBedDesc());
        result.setAdditionalBedId(source.getAdditionalBedId());
        result.setAdditionalFloorDesc(source.getAdditionalFloorDesc());
        result.setAdditionalFloorId(source.getAdditionalFloorId());
        result.setAdditionalRoomDesc(source.getAdditionalRoomDesc());
        result.setAdditionalRoomId(source.getAdditionalRoomId());
        result.setAdditionalUnitDesc(source.getAdditionalUnitDesc());
        result.setAdditionalUnitId(source.getAdditionalUnitId());
        result.setAdmissionSource(source.getAdmissionSource());
        result.setAdmissionSourceCode(source.getAdmissionSourceCode());
        result.setAdmissionType(source.getAdmissionType());
        result.setAdmissionTypeCode(source.getAdmissionTypeCode());
        result.setAdtRecordId(source.getAdtRecordId());
        result.setBedDesc(source.getBedDesc());
        result.setBedId(source.getBedId());
        result.setDestination(source.getDestination());
        result.setDestinationType(source.getDestinationType());
        result.setDischargeStatus(source.getDischargeStatus());
        result.setDischargeStatusCode(source.getDischargeStatusCode());
        result.setEffectiveDateTime(source.getEffectiveDateTime());
        result.setEnteredBy(source.getEnteredBy());
        result.setEnteredByPositionId(source.getEnteredByPositionId());
        result.setEnteredDate(source.getEnteredDate());
        result.setFloorDesc(source.getFloorDesc());
        result.setFloorId(source.getFloorId());
        result.setModifiedDateTime(source.getModifiedDateTime());
        result.setOrigin(source.getOrigin());
        result.setOriginType(source.getOriginType());
        result.setOutpatient(source.isOutpatient());
        result.setOutpatientStatus(source.getOutpatientStatus());
        result.setPatientId(source.getPatientId());
        result.setPayerCode(source.getPayerCode());
        result.setPayerName(source.getPayerName());
        result.setPayerType(source.getPayerType());
        result.setQhsWaiver(source.isQhsWaiver());
        result.setRoomDesc(source.getRoomDesc());
        result.setRoomId(source.getRoomId());
        result.setSkilledCare(source.isSkilledCare());
        result.setSkilledEffectiveFromDate(Optional.ofNullable(source.getSkilledEffectiveFromDate()).map(LocalDate::toString).orElse(null));
        result.setSkilledEffectiveToDate(Optional.ofNullable(source.getSkilledEffectiveToDate()).map(LocalDate::toString).orElse(null));
        result.setStandardActionType(source.getStandardActionType());
        result.setStopBillingDate(source.getStopBillingDate());
        result.setUnitDesc(source.getUnitDesc());
        result.setUnitId(source.getUnitId());
        result.setCancelledRecord(source.getIsCancelledRecord());

        result.setClientAdmitDate(client.getAdmitDate());
        result.setClientDischargeDate(client.getDischargeDate());
        result.setClientDeathDate(client.getDeathDate());
        result.setClientDeceased(client.getDeathIndicator());

        return result;
    }
}
