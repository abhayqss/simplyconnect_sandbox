package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.incident.ClassMemberType;
import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;
import com.scnsoft.eldermark.entity.incident.IncidentType;
import com.scnsoft.eldermark.entity.incident.IncidentTypeHelp;
import com.scnsoft.eldermark.entity.incident.Race;

import java.util.List;

public interface DirectoryService {
    
    List<ClassMemberType> getClassMemberTypes();

    List<IncidentPlaceType> getIncidentPlaceTypes();

    List<Race> getRaces();
    
    IncidentTypeHelp getIncidentTypeHelp(Integer incidentLevel);

    List<IncidentType> getIncidentTypes(Integer incidentLevel);

    List<CcdCode> getGenders();
}
