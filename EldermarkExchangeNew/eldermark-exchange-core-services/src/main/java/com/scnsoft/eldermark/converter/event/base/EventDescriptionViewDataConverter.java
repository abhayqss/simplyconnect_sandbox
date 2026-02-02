package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.dto.event.EventDescriptionViewData;
import com.scnsoft.eldermark.dto.event.PccEventAdtRecordDetails;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class EventDescriptionViewDataConverter<D extends EventDescriptionViewData> implements Converter<Event, D> {

    @Override
    public D convert(Event event) {
        var description = create();

        description.setLocation(event.getLocation());
        description.setHasInjury(event.getIsInjury());
        description.setSituation(event.getSituation());
        description.setBackground(event.getBackground());
        description.setAssessment(event.getAssessment());
        description.setIsFollowUpExpected(event.isFollowup());
        description.setFollowUpDetails(event.getFollowup());

        if (event.getPccAdtRecordEntity() != null) {
            var pccAdtEntity = event.getPccAdtRecordEntity();
            var pccDetails = new PccEventAdtRecordDetails();
            pccDetails.setAdmissionSource(pccAdtEntity.getAdmissionSource());
            pccDetails.setAdmissionType(pccAdtEntity.getAdmissionType());
            pccDetails.setBedDesc(pccAdtEntity.getBedDesc());
            pccDetails.setDestination(pccAdtEntity.getDestination());
            pccDetails.setDestinationType(pccAdtEntity.getDestinationType());
            pccDetails.setDischargeStatus(pccAdtEntity.getDischargeStatus());
            pccDetails.setEffectiveDateTime(DateTimeUtils.toEpochMilli(pccAdtEntity.getEffectiveDateTime()));
            pccDetails.setFloorDesc(pccAdtEntity.getFloorDesc());
            pccDetails.setOrigin(pccAdtEntity.getOrigin());
            pccDetails.setOriginType(pccAdtEntity.getOriginType());
            pccDetails.setOutpatient(pccAdtEntity.isOutpatient());
            pccDetails.setOutpatientStatus(pccAdtEntity.getOutpatientStatus());
            pccDetails.setRoomDesc(pccAdtEntity.getRoomDesc());
            pccDetails.setSkilledCare(pccAdtEntity.isSkilledCare());
            pccDetails.setSkilledEffectiveFromDate(pccAdtEntity.getSkilledEffectiveFromDate());
            pccDetails.setSkilledEffectiveToDate(pccAdtEntity.getSkilledEffectiveToDate());
            pccDetails.setUnitDesc(pccAdtEntity.getUnitDesc());
            pccDetails.setAdmitDate(DateTimeUtils.toEpochMilli(pccAdtEntity.getClientAdmitDate()));
            pccDetails.setDischargeDate(DateTimeUtils.toEpochMilli(pccAdtEntity.getClientDischargeDate()));
            pccDetails.setDeathDateTime(DateTimeUtils.toEpochMilli(pccAdtEntity.getClientDeathDate()));
            pccDetails.setDeceased(pccAdtEntity.getClientDeceased());
            description.setPccEventAdtRecordDetails(pccDetails);
        }

        return description;
    }

    protected abstract D create();
}
