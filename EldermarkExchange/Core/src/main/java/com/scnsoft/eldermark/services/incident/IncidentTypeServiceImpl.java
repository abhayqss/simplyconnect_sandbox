package com.scnsoft.eldermark.services.incident;

import com.scnsoft.eldermark.dao.incident.IncidentTypeDao;
import com.scnsoft.eldermark.entity.incident.IncidentType;
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
}
