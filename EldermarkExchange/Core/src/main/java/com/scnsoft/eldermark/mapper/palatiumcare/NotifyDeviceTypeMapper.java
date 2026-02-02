package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.DeviceType;
import com.scnsoft.eldermark.shared.palatiumcare.DeviceTypeDto;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;

public class NotifyDeviceTypeMapper extends GenericMapper<DeviceType, DeviceTypeDto> {

    @Override
    protected Class<DeviceType> getEntityClass() {
        return DeviceType.class;
    }

    @Override
    protected Class<DeviceTypeDto> getDtoClass() {
        return DeviceTypeDto.class;
    }
}
