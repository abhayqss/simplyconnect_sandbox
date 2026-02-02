package com.scnsoft.eldermark.dao.incident;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;

@Repository
public interface IncidentPlaceTypeDao extends JpaRepository<IncidentPlaceType, Long>{
    
    List<IncidentPlaceType> findByOrderByName();
    IncidentPlaceType getByName(String name);

}
