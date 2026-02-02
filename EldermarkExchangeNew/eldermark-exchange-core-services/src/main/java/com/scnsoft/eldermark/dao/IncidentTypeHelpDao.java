package com.scnsoft.eldermark.dao;


import com.scnsoft.eldermark.entity.event.incident.IncidentTypeHelp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentTypeHelpDao extends JpaRepository<IncidentTypeHelp, Long> {
    
    IncidentTypeHelp findByIncidentLevel(Integer incidentLevel);

}
