package com.scnsoft.eldermark.services.palatiumcare.location;

import com.google.gson.Gson;
import com.scnsoft.eldermark.dao.palatiumcare.LocationSyncDao;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.entity.palatiumcare.LocationLastChange;
import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotifyLocationSyncService {

    private LocationSyncDao locationSyncDao;

    @Autowired
    public void setLocationSyncDao(LocationSyncDao locationSyncDao) {
        this.locationSyncDao = locationSyncDao;
    }


    @Transactional
    public void removeRecord(Long recordId) {
        locationSyncDao.delete(recordId);
    }

    @Transactional
    public void removeRecordList(List<Long> recordIds) {
        List<LocationLastChange> locationLastChangeList = new ArrayList<>();
        for(Long recordId: recordIds) {
            LocationLastChange locationLastChange = new LocationLastChange();
            locationLastChange.setId(recordId);
            locationLastChangeList.add(locationLastChange);
        }
        locationSyncDao.delete(locationLastChangeList);
    }

    @Transactional(readOnly = true)
    public List<LocationLastChange> getChanges() {
        return EldermarkCollectionUtils.listFromIterable(locationSyncDao.findAll());
    }

    @Transactional
    public void changeRecordStatusToUpdated(Location location) {
        LocationLastChange record = locationSyncDao.findRecordByLocationId(location.getId());
        String jsonData = new Gson().toJson(location);
        record.setAction(EntityAction.UPDATED);
        record.setLocationId(location.getId());
        record.setJsonLocation(jsonData);
        locationSyncDao.save(record);
    }

    @Transactional
    public void addRecordWithStatusCreated(Location location) {
        LocationLastChange record = new LocationLastChange();
        String jsonData = new Gson().toJson(location);
        record.setAction(EntityAction.CREATED);
        record.setLocationId(location.getId());
        record.setJsonLocation(jsonData);
        System.out.println("Location: " + record);
        locationSyncDao.save(record);
    }

    @Transactional
    public void changeRecordStatusToRemoved(Long locationId) {
        LocationLastChange record = locationSyncDao.findRecordByLocationId(locationId);
        record.setAction(EntityAction.REMOVED);
        record.setJsonLocation(null);
        locationSyncDao.save(record);
    }
}
