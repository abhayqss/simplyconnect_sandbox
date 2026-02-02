package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.FacilityLastChange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacilitySyncDao extends CrudRepository<FacilityLastChange, Long> {

    FacilityLastChange findRecordByFacilityId(@Param("facilityId") Long facilityId);

    // void removeRecordsByFacilityIds(@Param("facilityIds") List<Long> facilityIds);

}

