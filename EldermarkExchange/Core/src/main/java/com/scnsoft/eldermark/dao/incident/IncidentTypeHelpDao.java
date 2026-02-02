package com.scnsoft.eldermark.dao.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.incident.IncidentTypeHelp;

@Repository
public interface IncidentTypeHelpDao extends JpaRepository<IncidentTypeHelp, Long>{
    
    IncidentTypeHelp findByIncidentLevel(Integer incidentLevel);

}
