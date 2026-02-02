package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.ResidentLastChange;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ResidentSyncDao extends CrudRepository<ResidentLastChange, Long> {

    ResidentLastChange findRecordByResidentId(@Param("residentId") Long residentId);

    @Modifying
    void removeRecordsByResidentIds(@Param("residentIds") List<Long> residentIds);

}
