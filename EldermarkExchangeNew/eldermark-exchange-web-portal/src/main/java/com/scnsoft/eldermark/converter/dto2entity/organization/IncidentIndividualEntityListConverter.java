package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.IncidentIndividualDto;
import com.scnsoft.eldermark.entity.event.incident.Individual;
import org.springframework.stereotype.Component;

@Component
public class IncidentIndividualEntityListConverter extends IncidentReportEntityListConverter<IncidentIndividualDto, Individual> {

    @Override
    public Individual convert(IncidentIndividualDto source) {
        Individual target = new Individual();
        target.setName(source.getName());
        target.setPhone(source.getPhone());
        target.setRelationship(source.getRelationship());
        return target;
    }
}
