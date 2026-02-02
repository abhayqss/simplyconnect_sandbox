package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.TextDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportIncidentTypeFreeText;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IncidentTypeFreeTextDtoConverter implements Converter<IncidentReportIncidentTypeFreeText, TextDto> {

    @Override
    public TextDto convert(IncidentReportIncidentTypeFreeText source) {
        if (source == null) {
            return null;
        }
        TextDto target = new TextDto();
        target.setId(source.getIncidentType().getId());
        target.setText(source.getFreeText() != null ? source.getFreeText().getFreeText() : null);
        return target;
    }
}
