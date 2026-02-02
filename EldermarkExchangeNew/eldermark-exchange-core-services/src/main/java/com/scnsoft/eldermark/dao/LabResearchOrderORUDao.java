package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderORU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LabResearchOrderORUDao extends JpaRepository<LabResearchOrderORU, Long> {

    @Modifying
    @Query("update LabResearchOrderORU" +
            " set success=false, labOrder=null, oru=null, errorMessage=:errorMessage" +
            " where id =:id")
    void updateOrderOruForFail(@Param("id") Long id, @Param("errorMessage") String errorMessage);
}
