package com.scnsoft.eldermark.converter.entity2dto.organization;


import com.scnsoft.eldermark.dto.IncidentLevelReportingSettingsDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentTypeHelp;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IncidentTypeHelpEntityToDtoConverter implements Converter<IncidentTypeHelp, IncidentLevelReportingSettingsDto> {

    @Override
    public IncidentLevelReportingSettingsDto convert(IncidentTypeHelp source) {
        return new IncidentLevelReportingSettingsDto(source.getId(), source.getIncidentLevel(), source.getReportingTimelines(), source.getFollowupRequirements());
    }

}
