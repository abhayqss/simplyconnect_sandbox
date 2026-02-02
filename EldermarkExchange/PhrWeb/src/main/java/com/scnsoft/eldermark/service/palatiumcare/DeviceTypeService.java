package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.palatiumcare.DeviceTypeDao;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyDeviceTypeMapper;
import com.scnsoft.eldermark.shared.palatiumcare.DeviceTypeDto;
import com.scnsoft.eldermark.entity.palatiumcare.DeviceType;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceTypeService extends BasicService<DeviceType, DeviceTypeDto> {

    private DeviceTypeDao deviceTypeDao;

    @Autowired
    public void setDeviceTypeDao(DeviceTypeDao deviceTypeDao) {
        this.deviceTypeDao = deviceTypeDao;
    }

    @Override
    protected GenericMapper<DeviceType, DeviceTypeDto> getMapper() {
        return new NotifyDeviceTypeMapper();
    }

    @Override
    protected CrudRepository<DeviceType, Long> getCrudRepository() {
        return deviceTypeDao;
    }


}
