package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.SocialHistoryObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SocialHistoryObservationDao extends JpaRepository<SocialHistoryObservation, Long> {
    @Query("SELECT sho FROM SocialHistoryObservation sho WHERE sho.id IN " +
            "(SELECT MIN(shoo.id) FROM SocialHistoryObservation shoo " +
            "WHERE shoo.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY shoo.type, shoo.freeText, shoo.freeTextValue)")
    Page<SocialHistoryObservation> listResidentsSocialHistoryObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds, Pageable pageable);

    @Query("SELECT COUNT(sho) FROM SocialHistoryObservation sho WHERE sho.id IN " +
            "(SELECT MIN(shoo.id) FROM SocialHistoryObservation shoo " +
            "WHERE shoo.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY shoo.type, shoo.freeText, shoo.freeTextValue)")
    Long countResidentsSocialHistoryObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
