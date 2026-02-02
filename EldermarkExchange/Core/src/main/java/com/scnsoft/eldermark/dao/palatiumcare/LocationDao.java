package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("notifyLocationDao")
public interface LocationDao extends CrudRepository<Location, Long> {

     Location findLocationByPalCareId(@Param("palCareId") Long palCareId);
}