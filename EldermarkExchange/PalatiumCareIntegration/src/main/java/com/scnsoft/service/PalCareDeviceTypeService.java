package com.scnsoft.service;

import com.scnsoft.dto.incoming.PalCareDeviceTypeDto;
import com.scnsoft.eldermark.dao.palatiumcare.DeviceTypeDao;
import com.scnsoft.eldermark.entity.palatiumcare.DeviceType;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class PalCareDeviceTypeService extends BasicService<DeviceType, PalCareDeviceTypeDto> {

    private DeviceTypeDao deviceTypeDao;

    @Autowired
    public void setDeviceTypeDao(DeviceTypeDao deviceTypeDao) {
        this.deviceTypeDao = deviceTypeDao;
    }

    @Override
    protected GenericMapper<DeviceType, PalCareDeviceTypeDto> getMapper() {
        return null;
    }

    @Override
    protected CrudRepository<DeviceType, Long> getCrudRepository() {
        return deviceTypeDao;
    }

}
