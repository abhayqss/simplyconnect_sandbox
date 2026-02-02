package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.TextDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportIncidentPlaceTypeFreeText;
import org.springframework.stereotype.Component;

@Component
public class IncidentPlaceTypeFreeTextDtoListConverter implements ListAndItemConverter<IncidentReportIncidentPlaceTypeFreeText, TextDto> {

    @Override
    public TextDto convert(IncidentReportIncidentPlaceTypeFreeText source) {
        if (source == null) {
            return null;
        }
        var target = new TextDto();
        target.setId(source.getIncidentPlaceType().getId());
        target.setText(source.getFreeText() != null ? source.getFreeText().getFreeText() : null);
        return target;
    }
}
