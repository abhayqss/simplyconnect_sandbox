package com.scnsoft.eldermark.dao;


import com.scnsoft.eldermark.entity.event.incident.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentTypeDao extends JpaRepository<IncidentType, Long> {
    List<IncidentType> findAll();

    List<IncidentType> findByIncidentLevel(Integer incidentLevel);

    IncidentType findFirstByIncidentLevelAndName(Integer incidentLevel, String name);
}
