package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.IncidentWitnessDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentWitness;
import org.springframework.stereotype.Component;

@Component
public class IncidentWitnessesDtoListConverter implements ListAndItemConverter<IncidentWitness, IncidentWitnessDto> {

    @Override
    public IncidentWitnessDto convert(IncidentWitness source) {
        if (source == null) {
            return null;
        }
        var target = new IncidentWitnessDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPhone(source.getPhone());
        target.setRelationship(source.getRelationship());
        target.setReport(source.getReport());
        return target;
    }
}
