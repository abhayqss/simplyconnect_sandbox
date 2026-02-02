package com.scnsoft.eldermark.mapper.palatiumcare.mobiledevice;

import com.scnsoft.eldermark.entity.palatiumcare.MobileDevice;
import com.scnsoft.eldermark.shared.palatiumcare.MobileDeviceDto;
import org.modelmapper.PropertyMap;

public class MobileDeviceToMobileDeviceDtoMap extends PropertyMap<MobileDevice, MobileDeviceDto> {

    @Override
    protected void configure() {
        map(source.getDeviceIdentifier(), destination.getDeviceIdentifier());
        map(source.getDeviceStatus(), destination.getDeviceStatus());
        map(source.getFacility().getName(), destination.getFacilityName());
    }
}
