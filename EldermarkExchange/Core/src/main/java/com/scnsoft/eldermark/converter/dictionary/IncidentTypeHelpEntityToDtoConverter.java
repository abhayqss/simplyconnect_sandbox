package com.scnsoft.eldermark.converter.dictionary;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.dictionary.IncidentLevelReportingSettingsDto;
import com.scnsoft.eldermark.entity.incident.IncidentTypeHelp;

@Component
public class IncidentTypeHelpEntityToDtoConverter implements Converter<IncidentTypeHelp, IncidentLevelReportingSettingsDto>{

    @Override
    public IncidentLevelReportingSettingsDto convert(IncidentTypeHelp source) {
        return new IncidentLevelReportingSettingsDto(source.getId(), source.getIncidentLevel(), source.getReportingTimelines(), source.getFollowupRequirements());
    }

}
