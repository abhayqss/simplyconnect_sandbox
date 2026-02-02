package com.scnsoft.service;

import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import com.scnsoft.eldermark.dao.palatiumcare.LocationDao;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class PalCareLocationService extends BasicService<Location, NotifyLocationDto> {

    private LocationDao locationDao;

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Override
    protected GenericMapper<Location, NotifyLocationDto> getMapper() {
        return null;
    }

    @Override
    protected CrudRepository<Location, Long> getCrudRepository() {
        return locationDao;
    }
}
