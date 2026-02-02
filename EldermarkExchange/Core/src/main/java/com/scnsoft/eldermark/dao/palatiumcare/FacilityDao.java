package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Facility;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("notifyFacilityDao")
public interface FacilityDao extends CrudRepository<Facility, Long> {

    Iterable<Facility> findFacilityByName(@Param("facilityName") String facilityName);
}
