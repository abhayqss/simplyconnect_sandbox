package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.incident.IncidentWeatherConditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentWeatherConditionTypeDao extends JpaRepository<IncidentWeatherConditionType, Long> {
}
