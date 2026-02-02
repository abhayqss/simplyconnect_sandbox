package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.TextDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentWeatherConditionTypeFreeText;
import org.springframework.stereotype.Component;

@Component
public class IncidentWeatherConditionTypeFreeTextDtoListConverter implements ListAndItemConverter<IncidentWeatherConditionTypeFreeText, TextDto> {

    @Override
    public TextDto convert(IncidentWeatherConditionTypeFreeText source) {
        if (source == null) {
            return null;
        }
        var target = new TextDto();
        target.setId(source.getIncidentWeatherConditionType().getId());
        target.setText(source.getFreeText() != null ? source.getFreeText().getFreeText() : null);
        return target;
    }
}
