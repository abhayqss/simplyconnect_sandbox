package com.scnsoft.eldermark.converter.incident;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.scnsoft.eldermark.dto.IndividualDto;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.entity.incident.Individual;

@Component
public class IndividualListDtoToEntityConverter implements Converter<IndividualDto, Individual> {

    @Override
    public Individual convert(IndividualDto source) {
        Individual target = new Individual();
        target.setName(source.getName());
        target.setPhone(source.getPhone());
        target.setRelationship(source.getRelationship());
        return target;
    }
    
    public List<Individual> convertList(List<IndividualDto> sourceList, IncidentReport incidentReport){
        List<Individual> targetList = new ArrayList<>();
        for (IndividualDto sourceItem : sourceList) {
            Individual targetItem = convert(sourceItem);
            targetItem.setIncidentReport(incidentReport);
            targetList.add(targetItem);
        }
        return targetList;
    }
}
