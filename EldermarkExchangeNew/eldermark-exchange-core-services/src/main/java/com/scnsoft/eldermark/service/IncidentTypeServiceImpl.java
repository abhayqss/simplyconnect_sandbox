package com.scnsoft.eldermark.service;


import com.scnsoft.eldermark.dao.IncidentTypeDao;
import com.scnsoft.eldermark.entity.event.incident.IncidentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IncidentTypeServiceImpl implements IncidentTypeService {

    @Autowired
    private IncidentTypeDao incidentTypeDao;

    @Override
    @Transactional(readOnly = true)
    public IncidentType getByIncidentLevelAndName(Integer incidentLevel, String name) {
        return incidentTypeDao.findFirstByIncidentLevelAndName(incidentLevel, name);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentType getById(Long id) {
        return incidentTypeDao.getOne(id);
    }
}
