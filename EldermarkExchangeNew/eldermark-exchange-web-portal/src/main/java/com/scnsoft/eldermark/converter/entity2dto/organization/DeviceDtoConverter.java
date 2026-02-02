package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.DeviceDetailDto;
import com.scnsoft.eldermark.entity.Device;

@Component
public class DeviceDtoConverter implements ListAndItemConverter<Device, DeviceDetailDto> {

    @Override
    public DeviceDetailDto convert(Device source) {
        DeviceDetailDto target = new DeviceDetailDto();
        target.setId(source.getId());
        target.setClientId(source.getClient().getId());
        target.setDeviceId(source.getDeviceId());
        target.setDeviceTypeName(source.getDeviceTypeId() != null ? source.getDeviceTypeId().getName() : null);
        target.setDeviceTypeId(source.getDeviceTypeId() != null ? source.getDeviceTypeId().getId() : null);
        target.setDateTime(source.getUpdatedOn() == null ? source.getCreatedOn() : source.getUpdatedOn());
        return target;
    }

}
