package com.scnsoft.eldermark.service;

import java.util.List;

import com.scnsoft.eldermark.entity.Device;

public interface DeviceService {
    
    Long save(Device device);

    Boolean deleteById(Long deviceId);

    List<Device> find(Long clientId);


}
