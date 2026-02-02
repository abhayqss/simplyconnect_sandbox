package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.IncidentIndividualDto;
import com.scnsoft.eldermark.entity.event.incident.Individual;
import org.springframework.stereotype.Component;

@Component
public class IncidentIndividualDtoListConverter implements ListAndItemConverter<Individual, IncidentIndividualDto> {

    @Override
    public IncidentIndividualDto convert(Individual source) {
        if (source == null) {
            return null;
        }
        var target = new IncidentIndividualDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPhone(source.getPhone());
        target.setRelationship(source.getRelationship());
        return target;
    }
}
