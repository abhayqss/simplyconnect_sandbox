package com.scnsoft.eldermark.converter.dto2entity.organization;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.CommunityDeviceTypeDto;
import com.scnsoft.eldermark.entity.community.DeviceType;
import com.scnsoft.eldermark.entity.community.DeviceTypeWorkflow;

@Component
public class CommunityDeviceTypeEntityConverter implements Converter<CommunityDeviceTypeDto, DeviceType>{
    
    @Override
    public DeviceType convert(CommunityDeviceTypeDto source) {
        var target = new DeviceType();
        target.setId(source.getId());
        target.setEnabled(source.getEnabled());
        target.setType(source.getType());
        target.setWorkflow(DeviceTypeWorkflow.valueOf(source.getWorkflow()));
        return target;
    }

}