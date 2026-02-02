package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.ResidentDevice;

import java.util.List;
import java.util.Set;

public interface ResidentDeviceService {
    ResidentDevice findIfUsedByAnotherResidentAndFacility(String deviceId, Long currentResidentId, Long facilityId);
    ResidentDevice findByDeviceIdAndFacilityId(String deviceId, Long facilityId);
    void delete(Long id);
}
