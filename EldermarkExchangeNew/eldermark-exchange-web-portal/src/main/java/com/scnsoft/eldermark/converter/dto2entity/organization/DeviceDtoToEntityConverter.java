package com.scnsoft.eldermark.converter.dto2entity.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dao.MedicalDeviceTypeDao;
import com.scnsoft.eldermark.dto.DeviceDto;
import com.scnsoft.eldermark.entity.Device;
import com.scnsoft.eldermark.service.ClientService;

@Component
public class DeviceDtoToEntityConverter implements Converter<DeviceDto, Device> {

    @Autowired
    private ClientService clientService;
    
    @Autowired
    MedicalDeviceTypeDao medicalDeviceTypeDao; 
    
    @Override
    public Device convert(DeviceDto source) {        
        Device target = new Device();
        target.setDeviceId(source.getDeviceId());        
        target.setId(source.getId());
        target.setDeviceTypeId(medicalDeviceTypeDao.findById(source.getDeviceTypeId()).get());
        target.setClient(clientService.getById(source.getClientId()));        
        return target;
    }   

}
