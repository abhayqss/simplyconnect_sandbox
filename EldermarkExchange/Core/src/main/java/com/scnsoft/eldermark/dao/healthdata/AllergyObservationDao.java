package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.AllergyObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * Spring Data version of {@link com.scnsoft.eldermark.dao.AllergyObservationDao AllergyObservationDao} repository.
 *
 * @author phomal
 * Created on 11/17/2017.
 */
@Repository
public interface AllergyObservationDao extends JpaRepository<AllergyObservation, Long> {

    String ALLERGY_IS_ACTIVE = "(lower(status.displayName) = 'active' OR (a.timeLow < current_date() AND (a.timeHigh IS NULL OR a.timeHigh > current_date())))";
    String ALLERGY_IS_INACTIVE = "(lower(status.displayName) = 'inactive')";
    String ALLERGY_IS_RESOLVED = "(lower(status.displayName) = 'resolved' OR a.timeHigh <= current_date())";

    Sort.Order ORDER_BY_START_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "timeLow");
    Sort.Order ORDER_BY_NAME = new Sort.Order(Sort.Direction.ASC, "productText");

    List<AllergyObservation> findByAllergyResidentId(Long residentId);

    Long countByAllergyResidentIdIn(Collection<Long> residentIds);

    @Query("SELECT ao FROM AllergyObservation ao " +
            "   INNER JOIN FETCH ao.allergy a " +
            "   LEFT JOIN FETCH ao.reactionObservations ro " +
            "   LEFT JOIN ao.observationStatusCode status " +
            "WHERE a.resident.id = :residentId AND (" +
            "(:active = true AND :inactive = true AND :resolved = true) OR " +
            "(:active = true AND " + ALLERGY_IS_ACTIVE + ") OR " +
            "(:inactive = true AND " + ALLERGY_IS_INACTIVE + ") OR " +
            "(:resolved = true AND " + ALLERGY_IS_RESOLVED + "))")
    List<AllergyObservation> listResidentAllergies(@Param("residentId") Long residentId,
                                                   @Param("active") boolean includeActive,
                                                   @Param("inactive") boolean includeInactive,
                                                   @Param("resolved") boolean includeResolved);

    @Query(value = "SELECT ao FROM AllergyObservation ao" +
            "   INNER JOIN FETCH ao.allergy a " +
            "   LEFT JOIN FETCH ao.reactionObservations ro " +
            "WHERE ao.id IN (" +
            "SELECT min(ao.id) FROM AllergyObservation ao " +
            "   INNER JOIN ao.allergy a " +
            "   LEFT JOIN ao.reactionObservations ro " +
            "   LEFT JOIN ao.observationStatusCode status " +
            "WHERE a.resident.id IN :residentIds AND (" +
            "   (:active = true AND :inactive = true AND :resolved = true) OR " +
            "   (:active = true AND " + ALLERGY_IS_ACTIVE + ") OR " +
            "   (:inactive = true AND " + ALLERGY_IS_INACTIVE + ") OR " +
            "   (:resolved = true AND " + ALLERGY_IS_RESOLVED + ")) " +
            "GROUP BY ao.productText, ao.observationStatusCode, ro.reactionText) " +
            "ORDER BY ao.productText ASC",
            countQuery = "SELECT count(ao) FROM AllergyObservation ao " +
                    "WHERE ao.id IN (" +
                    "SELECT min(ao.id) FROM AllergyObservation ao " +
                    "   INNER JOIN ao.allergy a " +
                    "   LEFT JOIN ao.reactionObservations ro " +
                    "   LEFT JOIN ao.observationStatusCode status " +
                    "WHERE a.resident.id IN :residentIds AND (" +
                    "   (:active = true AND :inactive = true AND :resolved = true) OR " +
                    "   (:active = true AND " + ALLERGY_IS_ACTIVE + ") OR " +
                    "   (:inactive = true AND " + ALLERGY_IS_INACTIVE + ") OR " +
                    "   (:resolved = true AND " + ALLERGY_IS_RESOLVED + ")) " +
                    "GROUP BY ao.productText, ao.observationStatusCode, ro.reactionText)")
    Page<AllergyObservation> listResidentAllergiesWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                    @Param("active") boolean includeActive,
                                                                    @Param("inactive") boolean includeInactive,
                                                                    @Param("resolved") boolean includeResolved,
                                                                    final Pageable pageable);

    @Query("SELECT count(ao) FROM AllergyObservation ao " +
            "WHERE ao.id IN (" +
            "SELECT min(ao.id) FROM AllergyObservation ao " +
            "   INNER JOIN ao.allergy a " +
            "   LEFT JOIN ao.reactionObservations ro " +
            "   LEFT JOIN ao.observationStatusCode status " +
            "WHERE a.resident.id IN :residentIds AND (" +
            ALLERGY_IS_ACTIVE + " OR " + ALLERGY_IS_INACTIVE + " OR " + ALLERGY_IS_RESOLVED + ") " +
            "GROUP BY ao.productText, ao.observationStatusCode, ro.reactionText)")
    Long countResidentAllergiesWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);

}
