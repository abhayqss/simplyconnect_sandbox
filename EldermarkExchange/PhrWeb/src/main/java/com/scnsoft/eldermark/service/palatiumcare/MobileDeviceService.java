package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.palatiumcare.FacilityDao;
import com.scnsoft.eldermark.dao.palatiumcare.MobileDeviceDao;
import com.scnsoft.eldermark.shared.palatiumcare.MobileDeviceDto;
import com.scnsoft.eldermark.entity.palatiumcare.Facility;
import com.scnsoft.eldermark.entity.palatiumcare.MobileDevice;
import com.scnsoft.eldermark.mapper.palatiumcare.mobiledevice.MobileDeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class MobileDeviceService {

    private MobileDeviceDao mobileDeviceDao;

    private FacilityDao facilityDao;

    private MobileDeviceMapper mobileDeviceMapper = new MobileDeviceMapper();

    @Autowired
    public void setMobileDeviceDao(@Qualifier("notifyMobileDeviceDao") MobileDeviceDao mobileDeviceDao) {
        this.mobileDeviceDao = mobileDeviceDao;
    }

    @Autowired
    public void setFacilityDao(FacilityDao facilityDao) {
        this.facilityDao = facilityDao;
    }

    private MobileDevice findMobileDeviceByUID(String deviceIdentifier) {
        Iterable<MobileDevice> existingMobileDevices = mobileDeviceDao.findByDeviceIdentifier(deviceIdentifier);
        if (existingMobileDevices != null && existingMobileDevices.iterator().hasNext()) {
            return existingMobileDevices.iterator().next();
        }
        return null;
    }


    @Transactional
    public String requestDeviceRegister(MobileDeviceDto mobileDeviceDto) {

        MobileDevice existingMobileDevice = findMobileDeviceByUID(mobileDeviceDto.getDeviceIdentifier());
        if(existingMobileDevice != null) {
            return existingMobileDevice.getDeviceStatus().toString();
        }

        MobileDevice mobileDevice = new MobileDevice();
        Iterable<Facility> facilities = facilityDao.findFacilityByName(mobileDeviceDto.getFacilityName());
        if(facilities != null) {
            Facility facility = facilities.iterator().hasNext() ? facilities.iterator().next() : null;
            mobileDevice.setFacility(facility);
        }
        mobileDevice.setDeviceIdentifier(mobileDeviceDto.getDeviceIdentifier());
        mobileDevice.setActive(false);
        mobileDevice.setDeviceStatus(MobileDevice.DeviceStatus.NOT_CONFIRMED_YET);
        MobileDevice responseDevice = mobileDeviceDao.save(mobileDevice);
        return responseDevice.getDeviceStatus().toString();
    }

    @Transactional
    public void activateDevice(Long deviceId) {
        mobileDeviceDao.activateDevice(deviceId);
    }

    @Transactional
    public void deactivateDevice(Long deviceId) {
        mobileDeviceDao.deactivateDevice(deviceId);
    }

    @Transactional(readOnly = true)
    public String getMobileDeviceStatusByUID(String deviceUID) {
        MobileDevice mobileDevice = findMobileDeviceByUID(deviceUID);
        if(mobileDevice != null) {
            return mobileDevice.getDeviceStatus().toString();
        }
        return MobileDevice.DeviceStatus.CONFIRMATION_NOT_REQUESTED.toString();
    }

    @Transactional(readOnly = true)
    public Iterable<MobileDevice> getMobileDeviceList() {
        return mobileDeviceDao.findAll();
    }


}
