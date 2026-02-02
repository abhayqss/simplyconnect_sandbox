package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.ResidentDeviceDao;
import com.scnsoft.eldermark.entity.ResidentDevice;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
public class ResidentDeviceServiceImpl implements  ResidentDeviceService {

    @Autowired
    private ResidentDeviceDao residentDeviceDao;

    @Override
    public ResidentDevice findIfUsedByAnotherResidentAndFacility(String deviceId, Long currentResidentId, Long facilityId) {
        List<ResidentDevice> residentDevices = residentDeviceDao.getAllByDeviceIdAndResident_Facility_IdAndResident_IdNot(deviceId, facilityId, currentResidentId);
        return findFirstResidentOrThrowIfMultipleCaseSensitive(deviceId, residentDevices);
    }

    @Override
    public ResidentDevice findByDeviceIdAndFacilityId(String deviceId, Long facilityId) {
        List<ResidentDevice> residentDevices = residentDeviceDao.getAllByDeviceIdAndResident_Facility_Id(deviceId, facilityId);
        return findFirstResidentOrThrowIfMultipleCaseSensitive(deviceId, residentDevices);
    }

    @Override
    public void delete(Long id) {
        residentDeviceDao.delete(id);
    }

    private ResidentDevice findFirstResidentOrThrowIfMultipleCaseSensitive(String deviceId, List<ResidentDevice> residentDevices) {
        int countOfResidentsThatUse = 0;
        ResidentDevice result = null;
        if (!CollectionUtils.isEmpty(residentDevices)) {
            for(ResidentDevice residentDevice : residentDevices) {
                if (residentDevice.getDeviceId().equals(deviceId)) {
                    result = residentDevice;
                    countOfResidentsThatUse++;
                }
            }
            if (countOfResidentsThatUse > 1) {
                throw new BusinessException("Device with id = " + deviceId + "is used by multiple residents");
            }
        }
        return result;
    }
}
