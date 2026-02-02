package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.DeviceDetailDto;
import com.scnsoft.eldermark.dto.DeviceDto;
import com.scnsoft.eldermark.entity.Device;
import com.scnsoft.eldermark.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceFacadeImpl implements DeviceFacade {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ListAndItemConverter<Device, DeviceDetailDto> deviceDtoConverter;

    @Autowired
    private Converter<DeviceDto, Device> deviceDtoToEntityConverter;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public List<DeviceDetailDto> find(@P("clientId") Long clientId) {
        return deviceDtoConverter.convertList(deviceService.find(clientId));
    }

    @Override
    @Transactional
    @PreAuthorize("@clientSecurityService.canEdit(#clientDeviceDto.clientId)")
    public Long save(@P("clientDeviceDto") DeviceDto clientDeviceDto) {
        return deviceService.save(deviceDtoToEntityConverter.convert(clientDeviceDto));
    }

    @Override
    @Transactional
    @PreAuthorize("@clientSecurityService.canEdit(@deviceDao.getOne(#deviceId).client.id)")
    public Boolean deleteById(Long deviceId) {
        return deviceService.deleteById(deviceId);
    }

}
