package com.scnsoft.eldermark.services.incident;

import com.scnsoft.eldermark.entity.incident.IncidentType;

public interface IncidentTypeService {
    IncidentType getByIncidentLevelAndName(Integer incidentLevel, String name);
}
