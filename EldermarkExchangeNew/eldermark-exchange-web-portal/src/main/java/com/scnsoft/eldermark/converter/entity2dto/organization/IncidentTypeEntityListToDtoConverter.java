package com.scnsoft.eldermark.converter.entity2dto.organization;


import com.scnsoft.eldermark.dto.IncidentTypeDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class IncidentTypeEntityListToDtoConverter implements Converter<List<IncidentType>, List<IncidentTypeDto>> {

    @Autowired
    private Converter<IncidentType, IncidentTypeDto> incidentTypeDtoConverter;

    @Override
    public List<IncidentTypeDto> convert(List<IncidentType> source) {
        Set<Long> convertedIncidentTypeIds = new HashSet<>();
        List<IncidentTypeDto> resultList = new ArrayList<>();
        convertIncidentTypeDto(resultList, source, convertedIncidentTypeIds);
        return resultList;
    }

    private void convertIncidentTypeDto(List<IncidentTypeDto> targetList, List<IncidentType> source, Set<Long> convertedIncidentTypeIds) {
        for (IncidentType item : source) {
            if (convertedIncidentTypeIds.contains(item.getId())) {
                continue;
            }
            convertedIncidentTypeIds.add(item.getId());
            IncidentTypeDto targetIncidentType = incidentTypeDtoConverter.convert(item);
            targetList.add(targetIncidentType);
            if (item.getChildIncidentTypes() != null) {
                targetIncidentType.setIncidentTypes(new ArrayList<IncidentTypeDto>());
                convertIncidentTypeDto(targetIncidentType.getIncidentTypes(), item.getChildIncidentTypes(), convertedIncidentTypeIds);
            }
        }
    }


}
