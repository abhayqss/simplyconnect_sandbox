package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.palatiumcare.DeviceDao;
import com.scnsoft.eldermark.entity.palatiumcare.Device;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyDeviceMapper;
import com.scnsoft.eldermark.shared.palatiumcare.DeviceDto;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceService extends BasicService<Device, DeviceDto> {

    private DeviceDao deviceDao;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Override
    protected GenericMapper<Device, DeviceDto> getMapper() {
        return new NotifyDeviceMapper();
    }

    @Override
    protected CrudRepository<Device, Long> getCrudRepository() {
        return deviceDao;
    }

}
