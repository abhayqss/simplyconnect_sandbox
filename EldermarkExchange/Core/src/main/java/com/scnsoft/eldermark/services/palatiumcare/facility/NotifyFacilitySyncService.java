package com.scnsoft.eldermark.services.palatiumcare.facility;

import com.google.gson.Gson;
import com.scnsoft.eldermark.dao.palatiumcare.FacilitySyncDao;
import com.scnsoft.eldermark.entity.palatiumcare.FacilityLastChange;
import com.scnsoft.eldermark.entity.palatiumcare.Facility;
import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotifyFacilitySyncService {

    private FacilitySyncDao facilitySyncDao;

    @Autowired
    public void setFacilitySyncDao(FacilitySyncDao facilitySyncDao) {
        this.facilitySyncDao = facilitySyncDao;
    }

    @Transactional
    public void removeRecord(Long recordId) {
        facilitySyncDao.delete(recordId);
    }

    @Transactional
    public void removeRecordList(List<Long> recordIds) {
        List<FacilityLastChange> facilityLastChangeList = new ArrayList<>();
        for(Long recordId: recordIds) {
            FacilityLastChange facilityLastChange = new FacilityLastChange();
            facilityLastChange.setFacilityId(recordId);
            facilityLastChangeList.add(facilityLastChange);
        }
        facilitySyncDao.delete(facilityLastChangeList);
    }

    @Transactional(readOnly = true)
    public List<FacilityLastChange> getChanges() {
        return EldermarkCollectionUtils.listFromIterable(facilitySyncDao.findAll());
    }

    @Transactional
    public void changeRecordStatusToUpdated(Facility facility) {
        FacilityLastChange record = facilitySyncDao.findRecordByFacilityId(facility.getId());
        String jsonData = new Gson().toJson(facility);
        record.setAction(EntityAction.UPDATED);
        record.setFacilityId(facility.getId());
        record.setJsonFacility(jsonData);
        facilitySyncDao.save(record);
    }

    @Transactional
    public void addRecordWithStatusCreated(Facility facility) {
        FacilityLastChange record = new FacilityLastChange();
        String jsonData = new Gson().toJson(facility);
        record.setAction(EntityAction.CREATED);
        record.setFacilityId(facility.getId());
        record.setJsonFacility(jsonData);
        facilitySyncDao.save(record);
    }

    @Transactional
    public void changeRecordStatusToRemoved(Long facilityId) {
        FacilityLastChange record = facilitySyncDao.findRecordByFacilityId(facilityId);
        record.setAction(EntityAction.REMOVED);
        record.setJsonFacility(null);
        facilitySyncDao.save(record);
    }
    
    
}
