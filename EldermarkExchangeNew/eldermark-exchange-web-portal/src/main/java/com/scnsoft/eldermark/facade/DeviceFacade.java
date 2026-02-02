package com.scnsoft.eldermark.facade;

import java.util.List;

import com.scnsoft.eldermark.dto.DeviceDetailDto;
import com.scnsoft.eldermark.dto.DeviceDto;

public interface DeviceFacade {

    List<DeviceDetailDto> find(Long clientId);

    Long save(DeviceDto clientDeviceDto);

    Boolean deleteById(Long deviceId);
}
