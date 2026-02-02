package com.scnsoft.eldermark.services.palatiumcare.location;

import com.scnsoft.eldermark.dao.palatiumcare.LocationDao;
import com.scnsoft.eldermark.entity.palatiumcare.Building;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyBuildingMapper;
import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationInDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationOutDto;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotifyExchangeLocationService {

    private LocationDao locationDao;

    private NotifyBuildingMapper notifyBuildingMapper = new NotifyBuildingMapper();

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    private Location transformInDtoToLocation(PalCareLocationInDto palCareLocationInDto) {
        Location location = new Location();
        Building building = notifyBuildingMapper.dtoToEntity(palCareLocationInDto.getBuilding());
        location.setPalCareId(palCareLocationInDto.getPalCareId());
        location.setBuilding(building);
        location.setRoom(palCareLocationInDto.getRoom());
        return location;
    }

    private PalCareLocationOutDto transformFromFromLocationToOutDto(Location location) {
        NotifyBuildingDto notifyBuildingDto = notifyBuildingMapper.entityToDto(location.getBuilding());
        return new PalCareLocationOutDto(location.getId(), location.getRoom(), notifyBuildingDto);
    }

    private List<PalCareLocationOutDto> transformToOutDtoList(List<Location> locationList) {
        List<PalCareLocationOutDto> dtoList = new ArrayList<>();
        for(Location location: locationList) {
            dtoList.add(transformFromFromLocationToOutDto(location));
        }
        return dtoList;
    }

    private List<Location> transformFromInDto(List<PalCareLocationInDto> dtoList) {
        List<Location> locationList = new ArrayList<>();
        for(PalCareLocationInDto dto: dtoList) {
            locationList.add(transformInDtoToLocation(dto));
        }
        return locationList;
    }

    @Transactional(readOnly = true)
    public List<PalCareLocationOutDto> getAllLocations() {
        Iterable<Location> locationIterable = locationDao.findAll();
        List<Location> locationList = EldermarkCollectionUtils.listFromIterable(locationIterable);
        return transformToOutDtoList(locationList);
    }

    @Transactional
    public void createLocation(PalCareLocationInDto palCareLocationInDto) {
        Location location = transformInDtoToLocation(palCareLocationInDto);
        locationDao.save(location);
    }

    @Transactional(readOnly = true)
    public PalCareLocationOutDto getLocation(Long id) {
        Location location = locationDao.findOne(id);
        return transformFromFromLocationToOutDto(location);
    }

    @Transactional
    public void addLocationList(List<PalCareLocationInDto> palCareResidentInDtoList) {
        List<Location> locationList = transformFromInDto(palCareResidentInDtoList);
        locationDao.save(locationList);
    }

}
