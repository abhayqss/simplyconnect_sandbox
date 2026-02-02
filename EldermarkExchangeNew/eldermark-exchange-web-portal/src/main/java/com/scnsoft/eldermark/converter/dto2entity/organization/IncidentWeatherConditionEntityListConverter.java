package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.TextDto;
import com.scnsoft.eldermark.entity.event.incident.FreeText;
import com.scnsoft.eldermark.entity.event.incident.IncidentWeatherConditionTypeFreeText;
import com.scnsoft.eldermark.service.IncidentWeatherConditionTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IncidentWeatherConditionEntityListConverter extends IncidentReportEntityListConverter<TextDto, IncidentWeatherConditionTypeFreeText> {

    @Autowired
    private IncidentWeatherConditionTypeService incidentWeatherConditionTypeService;

    @Override
    public IncidentWeatherConditionTypeFreeText convert(TextDto source) {
        var target = new IncidentWeatherConditionTypeFreeText();
        var incidentWeatherConditionType = incidentWeatherConditionTypeService.getById(source.getId());
        target.setIncidentWeatherConditionType(incidentWeatherConditionType);
        if (incidentWeatherConditionType.getFreeText()) {
            target.setFreeText(StringUtils.isNotEmpty(source.getText()) ? new FreeText(source.getText()) : null);
        }
        return target;
    }
}
