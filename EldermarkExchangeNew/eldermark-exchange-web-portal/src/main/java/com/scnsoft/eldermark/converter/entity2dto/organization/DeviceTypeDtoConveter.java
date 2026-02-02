package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.DeviceTypeDto;
import com.scnsoft.eldermark.entity.MedicalDeviceType;

@Component
public class DeviceTypeDtoConveter implements ListAndItemConverter<MedicalDeviceType, DeviceTypeDto> {

    @Override
    public DeviceTypeDto convert(MedicalDeviceType source) {
        DeviceTypeDto target = new DeviceTypeDto();
        target.setId(source.getId());
        target.setName(source.getName());        
        return target;
    }
}
