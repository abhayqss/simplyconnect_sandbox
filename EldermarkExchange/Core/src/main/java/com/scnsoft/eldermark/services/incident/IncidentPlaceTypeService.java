package com.scnsoft.eldermark.services.incident;

import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;

public interface IncidentPlaceTypeService {
    IncidentPlaceType getByName(String name);
}
