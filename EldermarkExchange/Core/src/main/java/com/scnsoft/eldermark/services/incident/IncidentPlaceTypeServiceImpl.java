package com.scnsoft.eldermark.services.incident;

import com.scnsoft.eldermark.dao.incident.IncidentPlaceTypeDao;
import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;
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
}
