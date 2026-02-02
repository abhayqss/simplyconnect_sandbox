package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.palatiumcare.LocationDao;
import com.scnsoft.eldermark.dao.palatiumcare.NotifyResidentCustomDao;
import com.scnsoft.eldermark.dao.palatiumcare.NotifyResidentDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.palatiumcare.Building;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyBuildingMapper;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyResidentMapper;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NotifyResidentService extends BasicService<NotifyResident, NotifyResidentDto> {

    private NotifyResidentDao notifyResidentDao;

    private ResidentDao residentDao;

    private NotifyResidentCustomDao notifyResidentCustomDao;

    private LocationDao locationDao;

    private DatabasesService databasesService;

    private NotifyBuildingMapper notifyBuildingMapper = new NotifyBuildingMapper();

    @Autowired
    public void setNotifyResidentDao(NotifyResidentDao notifyResidentDao) {
        this.notifyResidentDao = notifyResidentDao;
    }

    @Autowired
    public void setNotifyResidentCustomDao(NotifyResidentCustomDao notifyResidentCustomDao) {
        this.notifyResidentCustomDao = notifyResidentCustomDao;
    }

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Autowired
    public void setDatabasesService(DatabasesService databasesService) {
        this.databasesService = databasesService;
    }

    @Override
    protected GenericMapper<NotifyResident, NotifyResidentDto> getMapper() {
        return new NotifyResidentMapper();
    }

    @Override
    protected CrudRepository<NotifyResident, Long> getCrudRepository() {
        return notifyResidentDao;
    }

    private NotifyResident transformDtoToNotifyResident(NotifyResidentDto notifyResidentDto) {

        NotifyResident notifyResident = new NotifyResident();

        notifyResident.getResident().setFirstName(notifyResidentDto.getFirstName());
        notifyResident.getResident().setLastName(notifyResidentDto.getLastName());
        notifyResident.getResident().setActive(Boolean.TRUE);

        String legacyId = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        notifyResident.getResident().setLegacyId(legacyId);
        notifyResident.getResident().setLegacyTable("NotifyResident");

        Database unaffiliated = databasesService.getUnaffiliatedDatabase();
        notifyResident.getResident().setDatabase(unaffiliated);
        notifyResident.getResident().setDatabaseId(unaffiliated.getId());

        NotifyLocationDto locationDto = notifyResidentDto.getLocation();
        Location location = null;
        if(locationDto.getId() != null) {
            location = locationDao.findOne(locationDto.getId());
        } else {
            Building notifyBuildingDto = notifyBuildingMapper.dtoToEntity(locationDto.getBuilding());
            location = new Location(locationDto.getRoom(), notifyBuildingDto);
            location = locationDao.save(location);
        }
        notifyResident.setLocation(location);

        return notifyResident;
    }


    @Transactional
    public NotifyResident save(NotifyResidentDto notifyResidentDto) {
        NotifyResident notifyResident = transformDtoToNotifyResident(notifyResidentDto);
        Resident resident = notifyResident.getResident();
        Resident savedResident = residentDao.create(resident);
        if(savedResident != null) {
            return notifyResidentDao.save(notifyResident);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<NotifyResidentDto> getCareTeamMemberResidentList(Long employeeId, NotifyResidentFilter notifyResidentFilter, Pageable pageable) {
        List<NotifyResident> notifyResidentList = notifyResidentCustomDao.getCareTeamMemberResidentsByEmployeeId(employeeId, notifyResidentFilter, pageable);
        return getMapper().entityListToDtoList(notifyResidentList);
    }

    @Transactional(readOnly = true)
    public List<NotifyResidentDto> getCommunityResidentList(Long employeeId, NotifyResidentFilter notifyResidentFilter, Pageable pageable) {
        List<NotifyResident> notifyResidentList = notifyResidentCustomDao.getCommunityResidentsByEmployeeId(employeeId, notifyResidentFilter, pageable);
        return getMapper().entityListToDtoList(notifyResidentList);
    }

    @Transactional(readOnly = true)
    public List<NotifyResidentDto> getAllResidentsByEmployeeId(Long employeeId, NotifyResidentFilter notifyResidentFilter, Pageable pageable) {

        List<NotifyResident> notifyCareTeamMemberResidentList = notifyResidentCustomDao.getCareTeamMemberResidentsByEmployeeId(employeeId, notifyResidentFilter, pageable);
        List<NotifyResident> notifyCommunityResidentList = notifyResidentCustomDao.getCommunityResidentsByEmployeeId(employeeId, notifyResidentFilter, pageable);

        Map<Long, NotifyResident> notifyResidentMap = new HashMap<>();
        if(notifyCareTeamMemberResidentList != null) {
            for(NotifyResident notifyResident: notifyCareTeamMemberResidentList) {
                notifyResidentMap.put(notifyResident.getId(), notifyResident);
            }
        }
        if(notifyCommunityResidentList != null) {
            for(NotifyResident notifyResident: notifyCommunityResidentList) {
                notifyResidentMap.put(notifyResident.getId(), notifyResident);
            }
        }
        List<NotifyResident> resultList = new ArrayList<>();
        for(Map.Entry<Long, NotifyResident> notifyResidentEntry : notifyResidentMap.entrySet()) {
            NotifyResident notifyResident = notifyResidentEntry.getValue();
            resultList.add(notifyResident);
        }

        return getMapper().entityListToDtoList(resultList);
    }

}
