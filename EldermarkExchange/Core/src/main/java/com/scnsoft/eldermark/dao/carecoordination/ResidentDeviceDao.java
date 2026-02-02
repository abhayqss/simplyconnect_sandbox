package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.ResidentDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentDeviceDao extends JpaRepository<ResidentDevice, Long>, JpaSpecificationExecutor<ResidentDevice> {
    List<ResidentDevice> getAllByDeviceIdAndResident_Facility_IdAndResident_IdNot(String deviceId, Long facilityId, Long residentId);
    List<ResidentDevice> getAllByDeviceIdAndResident_Facility_Id(String deviceId, Long facilityId);
}
