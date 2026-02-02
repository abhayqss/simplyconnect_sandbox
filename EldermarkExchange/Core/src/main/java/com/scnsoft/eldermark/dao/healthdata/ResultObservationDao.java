package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.ResultObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ResultObservationDao extends JpaRepository<ResultObservation, Long>{
    @Query("SELECT res FROM ResultObservation res WHERE res.id IN " +
            "(SELECT MIN(ress.id) FROM ResultObservation ress " +
            "WHERE ress.result.resident.id IN (:residentIds) " +
            "GROUP BY ress.effectiveTime, ress.resultTypeCode.id, ress.statusCode, ress.value, ress.valueUnit)")
    Page<ResultObservation> listResidentResultsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds, Pageable pageable);

    @Query("SELECT COUNT(res) FROM ResultObservation res WHERE res.id IN " +
            "(SELECT MIN(ress.id) FROM ResultObservation ress " +
            "WHERE ress.result.resident.id IN (:residentIds) " +
            "GROUP BY ress.effectiveTime, ress.resultTypeCode.id, ress.statusCode, ress.value, ress.valueUnit)")
    Long countResidentResultsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
