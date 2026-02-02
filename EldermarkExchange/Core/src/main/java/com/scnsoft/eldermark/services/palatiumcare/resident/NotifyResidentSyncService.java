package com.scnsoft.eldermark.services.palatiumcare.resident;

import com.google.gson.Gson;
import com.scnsoft.eldermark.dao.palatiumcare.ResidentSyncDao;
import com.scnsoft.eldermark.entity.palatiumcare.Building;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.entity.palatiumcare.ResidentLastChange;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyBuildingMapper;
import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;
import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentDto;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotifyResidentSyncService {

    private ResidentSyncDao residentSyncDao;

    private NotifyBuildingMapper notifyBuildingMapper = new NotifyBuildingMapper();

    @Autowired
    public void setResidentSyncDao(ResidentSyncDao residentSyncDao) {
        this.residentSyncDao = residentSyncDao;
    }

    @Transactional
    public void removeRecord(Long recordId) {
        residentSyncDao.delete(recordId);
    }

    @Transactional
    public void removeRecordList(List<Long> recordIds) {
        residentSyncDao.removeRecordsByResidentIds(recordIds);
    }

    @Transactional(readOnly = true)
    public List<ResidentLastChange> getChanges() {
        return EldermarkCollectionUtils.listFromIterable(residentSyncDao.findAll());
    }

    private NotifyResidentDto transform(NotifyResident notifyResident) {
        NotifyResidentDto notifyResidentDto = new NotifyResidentDto();
        notifyResidentDto.setId(notifyResident.getResident().getId());
        notifyResidentDto.setFirstName(notifyResident.getResident().getFirstName());
        notifyResidentDto.setLastName(notifyResident.getResident().getLastName());

        Location location = new Location();
        Building building = location.getBuilding();
        NotifyBuildingDto notifyBuildingDto = notifyBuildingMapper.entityToDto(building);
        NotifyLocationDto notifyLocationDto = new NotifyLocationDto();
        notifyLocationDto.setId(location.getId());
        notifyLocationDto.setRoom(location.getRoom());
        notifyLocationDto.setBuilding(notifyBuildingDto);
        notifyResidentDto.setLocation(notifyLocationDto);
        return notifyResidentDto;
    }

    public void changeRecordStatusToUpdated(NotifyResident resident) {
        Long residentId = resident.getId();
        NotifyResidentDto residentDto = transform(resident);
        ResidentLastChange record = residentSyncDao.findRecordByResidentId(residentId);
        if(record != null) {
            String jsonData = new Gson().toJson(residentDto);
            record.setAction(EntityAction.UPDATED);
            record.setResidentId(resident.getId());
            record.setJsonResident(jsonData);
            residentSyncDao.save(record);
        }
    }

    public void addRecordWithStatusCreated(NotifyResident resident) {
        ResidentLastChange record = new ResidentLastChange();
        NotifyResidentDto notifyResidentDto = transform(resident);
        String jsonData = new Gson().toJson(notifyResidentDto);
        record.setAction(EntityAction.CREATED);
        record.setResidentId(resident.getId());
        record.setJsonResident(jsonData);
        residentSyncDao.save(record);
    }

    @Transactional
    public void changeRecordStatusToRemoved(Long residentId) {
        ResidentLastChange record = residentSyncDao.findRecordByResidentId(residentId);
        record.setAction(EntityAction.REMOVED);
        record.setJsonResident(null);
        residentSyncDao.save(record);
    }
}
