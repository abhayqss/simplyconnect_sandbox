package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.incident.IncidentPlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentPlaceTypeDao extends JpaRepository<IncidentPlaceType, Long> {
    
    List<IncidentPlaceType> findByOrderByName();
    IncidentPlaceType getByName(String name);

}
