package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.hl7.entity2dto.segment.PatientVisitViewDataConverter;
import com.scnsoft.eldermark.dto.events.PatientVisitDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ClientVisitDtoConverter extends PatientVisitViewDataConverter<PatientVisitDto> {

    @Override
    protected PatientVisitDto create() {
        return new PatientVisitDto();
    }
}
