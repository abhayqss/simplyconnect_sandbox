package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.incident.ClassMemberTypeDao;
import com.scnsoft.eldermark.dao.incident.IncidentPlaceTypeDao;
import com.scnsoft.eldermark.dao.incident.IncidentTypeDao;
import com.scnsoft.eldermark.dao.incident.IncidentTypeHelpDao;
import com.scnsoft.eldermark.dao.incident.RaceDao;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.incident.ClassMemberType;
import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;
import com.scnsoft.eldermark.entity.incident.IncidentType;
import com.scnsoft.eldermark.entity.incident.IncidentTypeHelp;
import com.scnsoft.eldermark.entity.incident.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly=true)
public class DirectoryServiceImpl implements DirectoryService {
    
    @Autowired
    private ClassMemberTypeDao classMemberTypeDao;
    
    @Autowired
    private IncidentPlaceTypeDao incidentPlaceTypeDao;
    
    @Autowired
    private RaceDao raceDao;
    
    @Autowired
    private IncidentTypeHelpDao incidentTypeHelpDao;
    
    @Autowired
    private IncidentTypeDao incidentTypeDao;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Override
    @Transactional(readOnly=true)
    public IncidentTypeHelp getIncidentTypeHelp(Integer incidentLevel) {
        return incidentTypeHelpDao.findByIncidentLevel(incidentLevel);
    }

    @Override
    @Transactional(readOnly=true)
    public List<IncidentType> getIncidentTypes(Integer incidentLevel) {
        if (incidentLevel == null) return incidentTypeDao.findAll();
        return incidentTypeDao.findByIncidentLevel(incidentLevel);
    }
    
    @Override
    @Transactional(readOnly=true)
    public List<ClassMemberType> getClassMemberTypes() {
        return classMemberTypeDao.findAll();
    }

    @Override
    @Transactional(readOnly=true)
    public List<IncidentPlaceType> getIncidentPlaceTypes() {
        List<IncidentPlaceType> incidentPlaceTypes= incidentPlaceTypeDao.findByOrderByName();
        moveOtherIncidentPlaceToEnd(incidentPlaceTypes);
        return incidentPlaceTypes;
    }
    
    private void moveOtherIncidentPlaceToEnd(List<IncidentPlaceType> list) {
        IncidentPlaceType itemToBeMoved=null;
        for (IncidentPlaceType item : list) {
            if(item.getName().contains("Other")) {
                itemToBeMoved = item;
                list.remove(item);
                break;
            }
        }
        list.add(itemToBeMoved);
    }

    @Override
    @Transactional(readOnly=true)
    public List<Race> getRaces() {
        List<Race> listRaces = raceDao.findByOrderByName();
        moveOtherRaceToEnd(listRaces);
        return listRaces;
    }
    
    private void moveOtherRaceToEnd(List<Race> list) {
        Race itemToBeMoved=null;
        for (Race item : list) {
            if(item.getName().contains("Other")) {
                itemToBeMoved = item;
                list.remove(item);
                break;
            }
        }
        list.add(itemToBeMoved);
    }

    @Override
    @Transactional(readOnly=true)
    public List<CcdCode> getGenders() {
        return ccdCodeDao.getGenders();
    }
}
