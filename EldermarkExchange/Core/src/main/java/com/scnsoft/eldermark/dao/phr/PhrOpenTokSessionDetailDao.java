package com.scnsoft.eldermark.dao.phr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.phr.PhrOpenTokSessionDetail;

@Repository
public interface PhrOpenTokSessionDetailDao  extends JpaRepository<PhrOpenTokSessionDetail, Long>{
    
    @Query("SELECT pv.id FROM PhrOpenTokSessionDetail pv  WHERE pv.opentokSession = :opentokSession ")
    Long getIdFromSession(@Param("opentokSession") String opentokSession);
    
    @Query("SELECT pv FROM PhrOpenTokSessionDetail pv  WHERE pv.opentokSession = :opentokSession ")
    PhrOpenTokSessionDetail getFromSession(@Param("opentokSession") String opentokSession);
}
