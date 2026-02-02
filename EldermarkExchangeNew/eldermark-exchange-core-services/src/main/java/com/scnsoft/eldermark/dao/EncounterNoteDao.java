package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.projection.EncounterNoteCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EncounterNoteDao extends AppJpaRepository<EncounterNote, Long>, CustomEncounterNoteDao {

    @Query("SELECT new com.scnsoft.eldermark.entity.projection.EncounterNoteCount(en.subType.encounterCode, COUNT(*)) FROM EncounterNote en"
            + " WHERE en.subType.encounterCode IS NOT NULL AND en.client.id = :clientId AND en.lastModifiedDate BETWEEN :fromDate"
            + " AND :toDate  GROUP BY en.subType.encounterCode")
    //todo delete and make with proper security like in ClientEventStatisticsServiceImpl
    List<EncounterNoteCount> count(@Param("clientId") Long clientId, @Param("fromDate") Date fromDate,  @Param("toDate") Date toDate);
}
