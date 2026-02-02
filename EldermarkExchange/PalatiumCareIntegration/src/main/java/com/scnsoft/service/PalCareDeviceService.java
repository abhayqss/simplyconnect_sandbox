package com.scnsoft.service;

import com.scnsoft.dto.incoming.PalCareDeviceDto;
import com.scnsoft.eldermark.dao.palatiumcare.DeviceDao;
import com.scnsoft.eldermark.entity.palatiumcare.Device;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class PalCareDeviceService extends BasicService<Device, PalCareDeviceDto> {

    private DeviceDao deviceDao;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Override
    protected GenericMapper<Device, PalCareDeviceDto> getMapper() {
        return null;
    }

    @Override
    protected CrudRepository<Device, Long> getCrudRepository() {
        return deviceDao;
    }
}
