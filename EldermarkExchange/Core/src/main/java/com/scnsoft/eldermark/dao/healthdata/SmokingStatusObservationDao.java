package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.SmokingStatusObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SmokingStatusObservationDao extends JpaRepository<SmokingStatusObservation, Long> {
    @Query("SELECT sso FROM SmokingStatusObservation sso WHERE sso.id IN " +
            "(SELECT MIN(ssoo.id) FROM SmokingStatusObservation ssoo " +
            "WHERE ssoo.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY ssoo.effectiveTimeLow, ssoo.value)")
    Page<SmokingStatusObservation> listResidentsSmokingStatusObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds, Pageable pageable);

    @Query("SELECT COUNT(sso) FROM SmokingStatusObservation sso WHERE sso.id IN " +
            "(SELECT MIN(ssoo.id) FROM SmokingStatusObservation ssoo " +
            "WHERE ssoo.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY ssoo.effectiveTimeLow, ssoo.value)")
    Long countResidentsSmokingStatusObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
