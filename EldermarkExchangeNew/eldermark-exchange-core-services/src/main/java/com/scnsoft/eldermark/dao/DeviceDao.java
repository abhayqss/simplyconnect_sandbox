package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.Device;

public interface DeviceDao extends JpaRepository<Device, Long> {

    List<Device> findByClient_id(Long clientId);

    Long countByDeviceId(String deviceTypeId);

    Long countByDeviceIdAndIdNotIn(String deviceTypeId, Iterable<Long> id);

}
