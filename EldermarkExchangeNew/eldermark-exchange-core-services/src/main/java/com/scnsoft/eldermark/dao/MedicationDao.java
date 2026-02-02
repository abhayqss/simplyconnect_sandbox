package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.medication.Medication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MedicationDao extends AppJpaRepository<Medication, Long> {

    String MEDICATION_IS_ACTIVE = "((m.medicationStarted IS NULL OR m.medicationStarted < current_date()) AND (m.medicationStopped IS NULL OR m.medicationStopped > current_date()))";

    String QUERY_FOR_listClientMedicationsWithoutDuplicates = "SELECT m FROM Medication m" +
            "   INNER JOIN FETCH m.medicationInformation mi " +
            "   LEFT JOIN FETCH mi.productNameCode mipnc " +
            "WHERE m.id IN (" +
            "SELECT min(m.id) FROM Medication m " +
            "   LEFT JOIN m.indications ind " +
            "   INNER JOIN m.medicationInformation mi " +
            "WHERE m.client.id IN :clientIds AND (" +
            "   (:active = true AND :inactive = true) OR " +
            "   (:active = true AND " + MEDICATION_IS_ACTIVE + ") OR " +
            "   (:inactive = true AND NOT " + MEDICATION_IS_ACTIVE + ")) " +
            "GROUP BY m.medicationStopped, m.medicationStarted, m.freeTextSig, m.statusCode, mi.productNameText, ind.value) " +
            "ORDER BY m.medicationStarted DESC";


    String COUNT_QUERY_FOR_listClientMedicationsWithoutDuplicates = "SELECT count(m) FROM Medication m " +
            //"   INNER JOIN m.medicationInformation mi " +
            "WHERE m.id IN (" +
            "SELECT min(m.id) FROM Medication m " +
            "   LEFT JOIN m.indications ind " +
            "   INNER JOIN m.medicationInformation mi " +
            "WHERE m.client.id IN :clientIds AND (" +
            "   (:active = true AND :inactive = true) OR " +
            "   (:active = true AND " + MEDICATION_IS_ACTIVE + ") OR " +
            "   (:inactive = true AND NOT " + MEDICATION_IS_ACTIVE + ")) " +
            "GROUP BY m.medicationStopped, m.medicationStarted, m.freeTextSig, m.statusCode, mi.productNameText, ind.value)";

    List<Medication> findByClient_IdIn(List<Long> clientIds);

    @Query(value = QUERY_FOR_listClientMedicationsWithoutDuplicates,
            countQuery = COUNT_QUERY_FOR_listClientMedicationsWithoutDuplicates)
    List<Medication> listResidentMedicationsWithoutDuplicates(@Param("clientIds") Collection<Long> clientIds,
                                                              @Param("active") boolean includeActive,
                                                              @Param("inactive") boolean includeInactive);

    void deleteAllByClientId(Long clientId);
}