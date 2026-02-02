package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.history.DeviceHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("notifyDeviceHistoryDao")
public interface DeviceHistoryDao extends CrudRepository<DeviceHistory, Long> {}

