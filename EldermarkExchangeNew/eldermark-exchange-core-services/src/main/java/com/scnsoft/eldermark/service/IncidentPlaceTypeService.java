package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.event.incident.IncidentPlaceType;

public interface IncidentPlaceTypeService {
    IncidentPlaceType getByName(String name);

    IncidentPlaceType getById(Long id);
}
