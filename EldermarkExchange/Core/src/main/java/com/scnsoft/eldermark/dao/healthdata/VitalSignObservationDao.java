package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.dao.projections.VitalSignTypeAndDate;
import com.scnsoft.eldermark.dao.projections.VitalSignTypeAndObservation;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * Spring Data version of {@link com.scnsoft.eldermark.dao.VitalSignDao VitalSignDao} and {@link com.scnsoft.eldermark.dao.VitalSignObservationDao VitalSignObservationDao} repositories.
 *
 * @author phomal
 * Created on 11/8/2017.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface VitalSignObservationDao extends JpaRepository<VitalSignObservation, Long> {

    Sort.Order ORDER_BY_DATE = new Sort.Order(Sort.Direction.ASC, "effectiveTime");
    Sort.Order ORDER_BY_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "effectiveTime");

    List<VitalSignObservation> findByVitalSignResidentId(Long residentId);

    Long countByVitalSignResidentId(Long residentId);

    @Query("SELECT vso FROM VitalSignObservation vso " +
            "WHERE vso.id IN (" +
            "SELECT min(vso.id) FROM VitalSignObservation vso " +
            "   INNER JOIN vso.vitalSign vs " +
            "   INNER JOIN vso.resultTypeCode ccd " +
            "WHERE vs.resident.id IN :residentIds AND ccd.code = :ccdCode AND (:fromDate IS NULL OR vso.effectiveTime >= :fromDate) AND (vso.effectiveTime <= :toDate) " +
            "GROUP BY vso.effectiveTime, vso.value, vso.unit, ccd.code)")
    List<VitalSignObservation> listResidentVitalSignObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                                  @Param("ccdCode") String vitalSignTypeCcdCode,
                                                                                  @Param("fromDate") Date fromDate,
                                                                                  @Param("toDate") Date toDate,
                                                                                  final Pageable pageable);

    @Query("SELECT count(vso) FROM VitalSignObservation vso " +
            "WHERE vso.id IN (" +
            "SELECT min(vso.id) FROM VitalSignObservation vso " +
            "   INNER JOIN vso.vitalSign vs " +
            "   INNER JOIN vso.resultTypeCode ccd " +
            "WHERE vs.resident.id IN :residentIds AND ccd.code IN :ccdCodes " +
            "GROUP BY vso.effectiveTime, vso.value, vso.unit, ccd.code)")
    Long countResidentVitalSignObservationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                             @Param("ccdCodes") Collection<String> vitalSignTypeCcdCodes);

    @Query("SELECT ccd.code AS type, min(vso.effectiveTime) AS date FROM VitalSignObservation vso " +
            "   INNER JOIN vso.vitalSign vs " +
            "   INNER JOIN vso.resultTypeCode ccd " +
            "WHERE vs.resident.id IN :residentIds " +
            "GROUP BY ccd.code")
    List<VitalSignTypeAndDate> listEarliestResidentVitalSignObservationDates(@Param("residentIds") Collection<Long> residentIds);

    // TODO use listLatestResidentVitalSignObservationsFast() when Spring Data + Hibernate will be updated
    @Query("SELECT vso FROM VitalSignObservation vso " +
            "   INNER JOIN FETCH vso.resultTypeCode ccd " +
            "WHERE vso.effectiveTime IN (" +
            "   SELECT max(vso2.effectiveTime) " +
            "   FROM VitalSignObservation vso2 " +
            "       INNER JOIN vso2.vitalSign vs " +
            "       INNER JOIN vso2.resultTypeCode ccd2 " +
            "   WHERE vs.resident.id IN :residentIds AND vso.resultTypeCode = vso2.resultTypeCode AND vso.id = vso2.id " +
            "   GROUP BY ccd2.code)")
    List<VitalSignObservation> listLatestResidentVitalSignObservations(@Param("residentIds") Collection<Long> residentIds);

    // https://jira.spring.io/browse/DATAJPA-980
    // Projections with native queries don't work as expected =(
    @Query(value =
            "SELECT q.* FROM (" +
                    "SELECT ccd.code AS [type], vso.effective_time AS [date], vso.value AS [value], vso.unit AS [unit], " +
                    "row_number() OVER (PARTITION BY ccd.code ORDER BY vso.effective_time DESC) AS rn " +
                    "FROM VitalSignObservation vso " +
                    "INNER JOIN VitalSign vs ON vs.id = vso.vital_sign_id " +
                    "INNER JOIN CcdCode ccd ON ccd.id = vso.result_type_code_id " +
                    "WHERE vs.resident_id IN :residentIds) AS q " +
                    "WHERE rn = 1", nativeQuery = true)
    List<VitalSignTypeAndObservation> listLatestResidentVitalSignObservationsFast(@Param("residentIds") Collection<Long> residentIds);

    @Query("SELECT vso FROM VitalSignObservation vso " +
            "   INNER JOIN vso.vitalSign vs " +
            "   INNER JOIN FETCH vso.resultTypeCode ccd " +
            "WHERE vs.resident.id IN :residentIds AND ccd.code = :ccdCode ")
    List<VitalSignObservation> listResidentVitalSignObservations(@Param("residentIds") Collection<Long> residentIds,
                                                                 @Param("ccdCode") String vitalSignTypeCcdCode,
                                                                 final Pageable pageable);

}
