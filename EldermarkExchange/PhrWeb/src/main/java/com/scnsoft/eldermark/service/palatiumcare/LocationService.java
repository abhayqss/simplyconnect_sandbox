package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.palatiumcare.LocationDao;
import com.scnsoft.eldermark.dao.palatiumcare.NotifyResidentDao;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyBuildingMapper;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyLocationMapper;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class LocationService extends BasicService<Location, NotifyLocationDto> {

    private LocationDao locationDao;

    private NotifyResidentDao notifyResidentDao;

    private NotifyBuildingMapper notifyBuildingMapper = new NotifyBuildingMapper();

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Autowired
    public void setNotifyResidentDao(NotifyResidentDao notifyResidentDao) {
        this.notifyResidentDao = notifyResidentDao;
    }

    @Override
    protected GenericMapper<Location, NotifyLocationDto> getMapper() {
        return new NotifyLocationMapper();
    }

    @Override
    protected CrudRepository<Location, Long> getCrudRepository() {
        return locationDao;
    }

    public NotifyLocationDto getLocationByResidentId(Long residentId) {
        NotifyResident resident = notifyResidentDao.findOne(residentId);
        if(resident != null) {
            Location location = resident.getLocation();
            NotifyBuildingDto notifyBuildingDto = notifyBuildingMapper.entityToDto(location.getBuilding());
            return new NotifyLocationDto(location.getRoom(), notifyBuildingDto);
        }
        return null;
    }

}
