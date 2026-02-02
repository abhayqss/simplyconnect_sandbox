package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DatabaseJpaDao extends JpaRepository<Database, Long> {

    Database findByOid(String oid);

    @Query(value = "select * from  SourceDatabase db " +
            "join Organization org on org.database_id = db.id " +
            "join Resident res on org.id = res.facility_id " +
            "join Event ev on ev.resident_id = res.id " +
            " where ev.adt_msg_id = :adtMsgId", nativeQuery = true)
    Database findByAdtMessageId(@Param("adtMsgId") Long adtMsgId);

    Database findByName(String oid);

}
