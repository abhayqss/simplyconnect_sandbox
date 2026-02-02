package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.IncidentVitalSignsDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentVitalSigns;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IncidentVitalSignsDtoConverter implements Converter<IncidentVitalSigns, IncidentVitalSignsDto> {

    @Override
    public IncidentVitalSignsDto convert(IncidentVitalSigns source) {
        if (source == null) {
            return null;
        }
        var target = new IncidentVitalSignsDto();
        target.setId(source.getId());
        target.setBloodPressure(source.getBloodPressure());
        target.setPulse(source.getPulse());
        target.setRespirationRate(source.getRespirationRate());
        target.setTemperature(source.getTemperature());
        target.setO2Saturation(source.getO2Saturation());
        target.setBloodSugar(source.getBloodSugar());
        return target;
    }
}
