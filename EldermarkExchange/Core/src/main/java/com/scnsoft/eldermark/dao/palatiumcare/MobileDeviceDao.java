package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.MobileDevice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("notifyMobileDeviceDao")
public interface MobileDeviceDao extends CrudRepository<MobileDevice, Long> {

    @Modifying
    void activateDevice(@Param("deviceId") Long deviceId);

    @Modifying
    void deactivateDevice(@Param("deviceId") Long deviceId);

    Iterable<MobileDevice> findByDeviceIdentifier(@Param("deviceUID") String deviceUID);

}
