package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.dto.dictionary.FreeTextKeyValueDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentLevelReportingSettingsDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentTypeDto;
import com.scnsoft.eldermark.shared.StateDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import java.util.List;

public interface DirectoryFacadeWeb {
    
    List<KeyValueDto> getClassMemberTypes();

    List<FreeTextKeyValueDto> getIncidentPlaceTypes();

    List<KeyValueDto> getRaces();
    
    IncidentLevelReportingSettingsDto getIncidentTypeHelp(Integer incidentLevel);

    List<IncidentTypeDto> getIncidentTypes(Integer incidentLevel);

    List<KeyValueDto> getGenders();

    List<StateDto> getStates();

}
