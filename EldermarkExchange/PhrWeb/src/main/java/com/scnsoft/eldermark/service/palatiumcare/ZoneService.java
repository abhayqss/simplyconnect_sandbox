package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.palatiumcare.ZoneDao;
import com.scnsoft.eldermark.mapper.ZoneDto;
import com.scnsoft.eldermark.entity.palatiumcare.Zone;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyZoneMapper;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class ZoneService extends BasicService<Zone, ZoneDto> {

    private ZoneDao zoneDao;

    @Autowired
    public void setZoneDao(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    @Override
    protected GenericMapper<Zone, ZoneDto> getMapper() {
        return new NotifyZoneMapper();
    }

    @Override
    protected CrudRepository<Zone, Long> getCrudRepository() {
        return zoneDao;
    }

}
