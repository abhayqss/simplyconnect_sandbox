package com.scnsoft.eldermark.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.DeviceDao;
import com.scnsoft.eldermark.entity.Device;
import com.scnsoft.eldermark.exception.BusinessException;

@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceDao deviceDao;

    @Override
    @Transactional
    public List<Device> find(Long clientId) {
        return deviceDao.findByClient_id(clientId);
    }

    @Override
    @Transactional
    public Long save(Device device) {
        if (device.getId() == null) {
            if (deviceDao.countByDeviceId(device.getDeviceId()) < 1) {
                device.setCreatedOn(new Date());
                return deviceDao.save(device).getId();
            }
            throw new BusinessException("Device type id already exists.");
        } else {
            if (deviceDao.countByDeviceIdAndIdNotIn(device.getDeviceId(), Collections.singletonList(device.getId())) < 1) {
                device.setUpdatedOn(new Date());
                return deviceDao.save(device).getId();
            }
            throw new BusinessException("Device type id already exists.");
        }
    }

    @Override
    @Transactional
    public Boolean deleteById(Long deviceId) {
        deviceDao.deleteById(deviceId);
        return true;
    }

}
