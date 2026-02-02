package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.FamilyHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository

public interface FamilyHistoryDao extends JpaRepository<FamilyHistory, Long> {

    @Query(value = "SELECT f FROM FamilyHistory f WHERE f.id IN " +
            "           (SELECT MIN(fh.id) FROM FamilyHistory fh " +
            "               LEFT JOIN fh.familyHistoryObservations fho" +
            "               WHERE fh.resident.id in :residentIds" +
            "               GROUP BY fho.deceased, fho.problemValue, fho.ageObservationValue, " +
            "                        fh.administrativeGenderCode, fho.problemTypeCode)")
    Page<FamilyHistory> listResidentFamilyHistoryWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                   Pageable pageable);

    @Query(value = "SELECT COUNT(f) FROM FamilyHistory f WHERE f.id IN " +
            "           (SELECT MIN(fh.id) FROM FamilyHistory fh " +
            "               LEFT JOIN fh.familyHistoryObservations fho" +
            "               WHERE fh.resident.id in :residentIds" +
            "               GROUP BY fho.deceased, fho.problemValue, fho.ageObservationValue," +
            "                        fh.administrativeGenderCode, fho.problemTypeCode)")
    Long countResidentFamilyHistoryWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);

}
