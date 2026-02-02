package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.IncidentVitalSignsDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentVitalSigns;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IncidentVitalSignsEntityConverter implements Converter<IncidentVitalSignsDto, IncidentVitalSigns> {

    @Override
    public IncidentVitalSigns convert(IncidentVitalSignsDto source) {
        if (source == null) {
            return null;
        }
        var target = new IncidentVitalSigns();
        target.setBloodPressure(source.getBloodPressure());
        target.setPulse(source.getPulse());
        target.setRespirationRate(source.getRespirationRate());
        target.setTemperature(source.getTemperature());
        target.setO2Saturation(source.getO2Saturation());
        target.setBloodSugar(source.getBloodSugar());
        return target;
    }
}