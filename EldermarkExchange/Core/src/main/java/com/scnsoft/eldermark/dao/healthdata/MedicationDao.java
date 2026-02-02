package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * Spring Data version of {@link com.scnsoft.eldermark.dao.MedicationDao MedicationDao} repository.
 *
 * @author phomal
 * Created on 10/9/2017.
 */
@Repository
public interface MedicationDao extends JpaRepository<Medication, Long> {

    String MEDICATION_IS_ACTIVE = "((m.medicationStarted IS NULL OR m.medicationStarted < current_date()) AND (m.medicationStopped IS NULL OR m.medicationStopped > current_date()))";

    String QUERY_FOR_listResidentMedicationsWithoutDuplicates = "SELECT m FROM Medication m" +
            "   INNER JOIN FETCH m.medicationInformation mi " +
            "   LEFT JOIN FETCH mi.productNameCode mipnc " +
            "WHERE m.id IN (" +
            "SELECT min(m.id) FROM Medication m " +
            "   LEFT JOIN m.indications ind " +
            "   INNER JOIN m.medicationInformation mi " +
            "WHERE m.resident.id IN :residentIds AND (" +
            "   (:active = true AND :inactive = true) OR " +
            "   (:active = true AND " + MEDICATION_IS_ACTIVE + ") OR " +
            "   (:inactive = true AND NOT " + MEDICATION_IS_ACTIVE + ")) " +
            "GROUP BY m.medicationStopped, m.medicationStarted, m.freeTextSig, m.statusCode, mi.productNameText, ind.value) " +
            "ORDER BY m.medicationStarted DESC";


    String COUNT_QUERY_FOR_listResidentMedicationsWithoutDuplicates = "SELECT count(m) FROM Medication m " +
            //"   INNER JOIN m.medicationInformation mi " +
            "WHERE m.id IN (" +
            "SELECT min(m.id) FROM Medication m " +
            "   LEFT JOIN m.indications ind " +
            "   INNER JOIN m.medicationInformation mi " +
            "WHERE m.resident.id IN :residentIds AND (" +
            "   (:active = true AND :inactive = true) OR " +
            "   (:active = true AND " + MEDICATION_IS_ACTIVE + ") OR " +
            "   (:inactive = true AND NOT " + MEDICATION_IS_ACTIVE + ")) " +
            "GROUP BY m.medicationStopped, m.medicationStarted, m.freeTextSig, m.statusCode, mi.productNameText, ind.value)";


    List<Medication> findByResidentId(Long residentId);

    Long countByResidentIdIn(Collection<Long> residentIds);

    @Query("SELECT m FROM Medication m JOIN m.medicationInformation mi " +
            "WHERE m.resident.id = :residentId AND " +
            "((:active = true AND :inactive = true) OR " +
            "(:active = true AND " + MEDICATION_IS_ACTIVE + ") OR " +
            "(:inactive = true AND NOT " + MEDICATION_IS_ACTIVE + "))")
    Page<Medication> listResidentMedications(@Param("residentId") Long residentId,
                                             @Param("active") boolean includeActive,
                                             @Param("inactive") boolean includeInactive,
                                             Pageable pageable);

    @Query(value = QUERY_FOR_listResidentMedicationsWithoutDuplicates,
            countQuery = COUNT_QUERY_FOR_listResidentMedicationsWithoutDuplicates)
    Page<Medication> listResidentMedicationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                              @Param("active") boolean includeActive,
                                                              @Param("inactive") boolean includeInactive,
                                                              Pageable pageable);

    @Query(value = QUERY_FOR_listResidentMedicationsWithoutDuplicates,
            countQuery = COUNT_QUERY_FOR_listResidentMedicationsWithoutDuplicates)
    List<Medication> listResidentMedicationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                              @Param("active") boolean includeActive,
                                                              @Param("inactive") boolean includeInactive);

    @Query("SELECT count(m) FROM Medication m " +
            //"   INNER JOIN m.medicationInformation mi " +
            "WHERE m.id IN (" +
            "SELECT min(m.id) FROM Medication m " +
            "   LEFT JOIN m.indications ind " +
            "   INNER JOIN m.medicationInformation mi " +
            "WHERE m.resident.id IN :residentIds " +
            "GROUP BY m.medicationStopped, m.medicationStarted, m.freeTextSig, m.statusCode, mi.productNameText, ind.value)")
    Long countResidentMedicationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);

}
