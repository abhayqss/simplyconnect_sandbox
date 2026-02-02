package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.DeviceType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("notifyDeviceTypeDao")
public interface DeviceTypeDao extends CrudRepository<DeviceType, Long> {}
