package com.scnsoft.eldermark.service;


import com.scnsoft.eldermark.entity.event.incident.IncidentType;

public interface IncidentTypeService {
    IncidentType getByIncidentLevelAndName(Integer incidentLevel, String name);

    IncidentType getById(Long id);
}
