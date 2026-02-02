package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.IncidentWitnessDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentWitness;
import org.springframework.stereotype.Component;

@Component
public class IncidentWitnessEntityListConverter extends IncidentReportEntityListConverter<IncidentWitnessDto, IncidentWitness> {

    @Override
    public IncidentWitness convert(IncidentWitnessDto source) {
        var target = new IncidentWitness();
        target.setName(source.getName());
        target.setPhone(source.getPhone());
        target.setRelationship(source.getRelationship());
        target.setReport(source.getReport());
        return target;
    }
}