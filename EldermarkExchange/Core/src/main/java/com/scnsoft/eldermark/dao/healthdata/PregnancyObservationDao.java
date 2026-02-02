package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.PregnancyObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PregnancyObservationDao extends JpaRepository<PregnancyObservation, Long> {
    @Query("SELECT po FROM PregnancyObservation po WHERE po.id IN " +
            "(SELECT MIN(poo.id) FROM PregnancyObservation poo " +
            "WHERE po.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY poo.effectiveTimeLow, poo.estimatedDateOfDelivery)")
    Page<PregnancyObservation> listResidentsPregnancyObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds, Pageable pageable);

    @Query("SELECT COUNT(po) FROM PregnancyObservation po WHERE po.id IN " +
            "(SELECT MIN(poo.id) FROM PregnancyObservation poo " +
            "WHERE po.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY poo.effectiveTimeLow, poo.estimatedDateOfDelivery)")
    Long countResidentsPregnancyObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
