package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.TextDto;
import com.scnsoft.eldermark.entity.event.incident.FreeText;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportIncidentPlaceTypeFreeText;
import com.scnsoft.eldermark.service.IncidentPlaceTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IncidentPlaceTypeFreeTextEntityListConverter extends IncidentReportEntityListConverter<TextDto, IncidentReportIncidentPlaceTypeFreeText> {

    @Autowired
    private IncidentPlaceTypeService incidentPlaceTypeService;

    @Override
    public IncidentReportIncidentPlaceTypeFreeText convert(TextDto source) {
        IncidentReportIncidentPlaceTypeFreeText target = new IncidentReportIncidentPlaceTypeFreeText();
        var incidentPlaceType = incidentPlaceTypeService.getById(source.getId());
        target.setIncidentPlaceType(incidentPlaceType);
        if (incidentPlaceType.getFreeText()) {
            target.setFreeText(StringUtils.isNotEmpty(source.getText()) ? new FreeText(source.getText()) : null);
        }
        return target;
    }
}
