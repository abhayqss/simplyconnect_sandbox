package com.scnsoft.eldermark.converter.event;

import com.scnsoft.eldermark.converter.event.base.TreatmentViewDataConverter;
import com.scnsoft.eldermark.dto.notification.event.TreatingHospitalMailDto;
import com.scnsoft.eldermark.dto.notification.event.TreatingPhysicianMailDto;
import com.scnsoft.eldermark.dto.notification.event.TreatmentDetailsNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class TreatmentDetailsNotificationDtoConverter extends TreatmentViewDataConverter<TreatingPhysicianMailDto, TreatingHospitalMailDto, TreatmentDetailsNotificationDto> {

    @Override
    protected TreatmentDetailsNotificationDto create() {
        return new TreatmentDetailsNotificationDto();
    }

    @Override
    protected TreatingPhysicianMailDto createPhysician() {
        return new TreatingPhysicianMailDto();
    }

    @Override
    protected TreatingHospitalMailDto createHospital() {
        return new TreatingHospitalMailDto();
    }
}
