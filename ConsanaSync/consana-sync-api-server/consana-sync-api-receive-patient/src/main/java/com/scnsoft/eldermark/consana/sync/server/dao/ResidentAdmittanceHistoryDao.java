package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.ResidentAdmittanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResidentAdmittanceHistoryDao extends JpaRepository<ResidentAdmittanceHistory, Long> {

    Long countByResidentId(Long residentId);

    @Query("SELECT coalesce(max(t.id), 0) FROM ResidentAdmittanceHistory t")
    Long getMaxId();

}
