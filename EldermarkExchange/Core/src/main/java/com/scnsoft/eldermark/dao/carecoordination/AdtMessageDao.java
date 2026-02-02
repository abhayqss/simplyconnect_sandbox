package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * @author sparuchnik
 */
public interface AdtMessageDao extends JpaRepository<AdtMessage, Long> {

    @Query(value = "select a from AdtMessage a" +
            " where id in (select e.adtMsgId from Event e where e.resident.id = :residentId)")
    List<AdtMessage> findByResidentId(@Param("residentId") Long residentId);

}
