package com.scnsoft.eldermark.converter.hl7.entity2dto.segment;

import com.scnsoft.eldermark.dto.adt.segment.PatientVisitNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PatientVisitNotificationDtoConverter extends PatientVisitViewDataConverter<PatientVisitNotificationDto> {

    @Override
    protected PatientVisitNotificationDto create() {
        return new PatientVisitNotificationDto();
    }

}
