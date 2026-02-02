package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.Encounter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface EncounterDao extends JpaRepository<Encounter, Long> {

    @Query(value = "SELECT e " +
            "   FROM Encounter e " +
            "   where e.resident.id in " +
             "     (SELECT min(e2.resident.id) FROM Encounter e2 " +
             "      WHERE e2.resident.id IN :residentIds" +
             "      group by e2.effectiveTime, e2.encounterTypeText)"
    )
    Page<Encounter> listResidentEncounters(@Param("residentIds") Collection<Long> residentIds,
                                           final Pageable pageable);

    @Query(value = "SELECT count(e) " +
            "   FROM Encounter e " +
            "   where e.resident.id in " +
            "     (SELECT min(e2.resident.id) FROM Encounter e2 " +
            "      WHERE e2.resident.id IN :residentIds" +
            "      group by e2.effectiveTime, e2.encounterTypeText)")
    Long countResidentEncountersWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
