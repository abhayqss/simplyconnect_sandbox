package com.scnsoft.eldermark.mapper.palatiumcare.mobiledevice;

import com.scnsoft.eldermark.shared.palatiumcare.MobileDeviceDto;
import com.scnsoft.eldermark.entity.palatiumcare.MobileDevice;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;

public class MobileDeviceMapper extends GenericMapper<MobileDevice, MobileDeviceDto> {

    {
        getModelMapper().addMappings(new MobileDeviceDtoToMobileDeviceMap());
        getModelMapper().addMappings(new MobileDeviceToMobileDeviceDtoMap());
    }

    @Override
    protected Class<MobileDevice> getEntityClass() {
        return MobileDevice.class;
    }

    @Override
    protected Class<MobileDeviceDto> getDtoClass() {
        return MobileDeviceDto.class;
    }
}
