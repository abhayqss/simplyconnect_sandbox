package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Device;
import com.scnsoft.eldermark.shared.palatiumcare.DeviceDto;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;

public class NotifyDeviceMapper extends GenericMapper<Device, DeviceDto> {

    @Override
    protected Class<Device> getEntityClass() {
        return Device.class;
    }

    @Override
    protected Class<DeviceDto> getDtoClass() {
        return DeviceDto.class;
    }
}
