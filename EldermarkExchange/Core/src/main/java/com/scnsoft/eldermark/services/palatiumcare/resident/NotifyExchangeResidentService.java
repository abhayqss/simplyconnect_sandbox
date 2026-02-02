package com.scnsoft.eldermark.services.palatiumcare.resident;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.palatiumcare.LocationDao;
import com.scnsoft.eldermark.dao.palatiumcare.NotifyResidentDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.palatiumcare.Building;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyBuildingMapper;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationInDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationOutDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.PalCareResidentInDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.PalCareResidentOutDto;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotifyExchangeResidentService {

    private NotifyResidentDao notifyResidentDao;

    private DatabasesService databasesService;

    private LocationDao locationDao;

    private static NotifyBuildingMapper notifyBuildingMapper = new NotifyBuildingMapper();

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Autowired
    public void setNotifyResidentDao(NotifyResidentDao notifyResidentDao) {
        this.notifyResidentDao = notifyResidentDao;
    }

    @Autowired
    public void setDatabasesService(DatabasesService databasesService) {
        this.databasesService = databasesService;
    }

    private NotifyResident transformInDtoToNotifyResident(PalCareResidentInDto palCareResidentInDto) {

        NotifyResident notifyResident = new NotifyResident();
        Resident resident = new Resident();

        resident.setFirstName(palCareResidentInDto.getFirstName());
        resident.setLastName(palCareResidentInDto.getFirstName());
        resident.setActive(Boolean.TRUE);
        resident.setLegacyId(palCareResidentInDto.getPalCareId().toString());
        resident.setLegacyTable("NotifyResident");
        notifyResident.setPalCareId(palCareResidentInDto.getPalCareId());

        Database unaffiliated = databasesService.getUnaffiliatedDatabase();
        resident.setDatabase(unaffiliated);
        resident.setDatabaseId(unaffiliated.getId());

        PalCareLocationInDto locationInDto = palCareResidentInDto.getPalCareLocationInDto();
        Building building =  notifyBuildingMapper.dtoToEntity(locationInDto.getBuilding());

        Location location = null;
        if(locationInDto.getPalCareId() != null) {
            location = locationDao.findLocationByPalCareId(locationInDto.getPalCareId());
        }
        if(locationInDto.getPalCareId() == null || location == null) {
            location = new Location(locationInDto.getRoom(), building);
            location = locationDao.save(location);
        }

        notifyResident.setLocation(location);
        return notifyResident;
    }


    @Transactional
    public void createNotifyResident(PalCareResidentInDto palCareResidentInDto) {
        NotifyResident notifyResident = transformInDtoToNotifyResident(palCareResidentInDto);
        notifyResidentDao.save(notifyResident);
    }

    @Transactional
    public void addNotifyResidentList(List<PalCareResidentInDto> palCareResidentInDtoList)  {
        List<NotifyResident> notifyResidentList = new ArrayList<>();
        for(PalCareResidentInDto dto : palCareResidentInDtoList) {
            NotifyResident notifyResident = transformInDtoToNotifyResident(dto);
            notifyResidentList.add(notifyResident);
        }
        notifyResidentDao.save(notifyResidentList);
    }

    public List<PalCareResidentOutDto> getAllNotifyResidents() {
        Iterable<NotifyResident> notifyResidentIterable = notifyResidentDao.findAll();
        List<NotifyResident> notifyResidentList = EldermarkCollectionUtils.listFromIterable(notifyResidentIterable);
        return transformList(notifyResidentList);
    }

    public PalCareResidentOutDto getNotifyResident(Long id) {
        NotifyResident notifyResident = notifyResidentDao.findOne(id);
        return transform(notifyResident);
    }

    public static PalCareResidentOutDto transform(NotifyResident notifyResident) {
        Location location = notifyResident.getLocation();
        Building building = location != null ? location.getBuilding() : null;
        NotifyBuildingDto notifyBuildingDto = building != null ? notifyBuildingMapper.entityToDto(building) : null;
        return new PalCareResidentOutDto(notifyResident.getId(),
                notifyResident.getResident().getFirstName(),
                notifyResident.getResident().getLastName(),
                new PalCareLocationOutDto(location.getId(), location.getRoom(), notifyBuildingDto)
        );
    }

    private static List<PalCareResidentOutDto> transformList(List<NotifyResident> notifyResidentList) {
        List<PalCareResidentOutDto> dtoList = new ArrayList<>();
        for(NotifyResident notifyResident: notifyResidentList) {
            dtoList.add(transform(notifyResident));
        }
        return dtoList;
    }
}
