package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.IncidentPlaceTypeDao;
import com.scnsoft.eldermark.entity.event.incident.IncidentPlaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IncidentPlaceTypeServiceImpl implements IncidentPlaceTypeService {

    @Autowired
    private IncidentPlaceTypeDao incidentPlaceTypeDao;

    @Override
    @Transactional(readOnly = true)
    public IncidentPlaceType getByName(String name) {
        return incidentPlaceTypeDao.getByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentPlaceType getById(Long id) {
        return incidentPlaceTypeDao.getOne(id);
    }
}
