package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.IncidentWeatherConditionTypeDao;
import com.scnsoft.eldermark.entity.event.incident.IncidentWeatherConditionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IncidentWeatherConditionTypeServiceImpl implements IncidentWeatherConditionTypeService {

    @Autowired
    private IncidentWeatherConditionTypeDao incidentWeatherConditionTypeDao;

    @Override
    public IncidentWeatherConditionType getById(Long id) {
        return incidentWeatherConditionTypeDao.getOne(id);
    }
}
