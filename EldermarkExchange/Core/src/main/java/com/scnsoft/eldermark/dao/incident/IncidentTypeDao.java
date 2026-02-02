package com.scnsoft.eldermark.dao.incident;

import com.scnsoft.eldermark.entity.incident.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentTypeDao extends JpaRepository<IncidentType, Long>{
    List<IncidentType> findAll();
    List<IncidentType> findByIncidentLevel(Integer incidentLevel);
    IncidentType findFirstByIncidentLevelAndName(Integer incidentLevel, String name);
}
