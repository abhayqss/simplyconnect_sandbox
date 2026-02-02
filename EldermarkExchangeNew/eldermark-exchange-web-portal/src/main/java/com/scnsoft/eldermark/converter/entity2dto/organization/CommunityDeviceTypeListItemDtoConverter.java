package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.CommunityDeviceTypeDto;
import com.scnsoft.eldermark.entity.community.DeviceType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CommunityDeviceTypeListItemDtoConverter implements ListAndItemConverter<DeviceType, CommunityDeviceTypeDto>{

    @Override
    public CommunityDeviceTypeDto convert(DeviceType source) {
        var target = new CommunityDeviceTypeDto();
        
        target.setId(source.getId());
        target.setType(source.getType());
        target.setWorkflow(source.getWorkflow().getDisplayName());
        target.setAutoCloseIntervalId(source.getAutoCloseInterval().getId());
        target.setAutoCloseIntervalDisplayName(source.getAutoCloseInterval().getName());
        target.setEnabled(source.getEnabled());
        
        return target;
    }

}
