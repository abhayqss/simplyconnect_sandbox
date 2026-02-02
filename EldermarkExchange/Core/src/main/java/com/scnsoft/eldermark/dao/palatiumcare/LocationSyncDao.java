package com.scnsoft.eldermark.dao.palatiumcare;
import com.scnsoft.eldermark.entity.palatiumcare.LocationLastChange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface LocationSyncDao extends CrudRepository<LocationLastChange, Long> {

    LocationLastChange findRecordByLocationId(@Param("locationId") Long locationId);

}
