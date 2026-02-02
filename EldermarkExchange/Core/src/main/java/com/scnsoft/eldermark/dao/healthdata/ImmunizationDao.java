package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.Immunization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;


/**
 * Spring Data version of {@link com.scnsoft.eldermark.dao.ImmunizationDao ImmunizationDao} repository.
 *
 * @author phomal
 * Created on 11/4/2017.
 */
@Repository
public interface ImmunizationDao extends JpaRepository<Immunization, Long> {

    Sort.Order ORDER_BY_START_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "immunizationStarted");
    Sort.Order ORDER_BY_NAME = new Sort.Order(Sort.Direction.ASC, "imi.text");

    Immunization findByResidentId(Long residentId);

    Long countByResidentIdIn(Collection<Long> residentIds);

    @Query(value = "SELECT i FROM Immunization i " +
            "   INNER JOIN FETCH i.immunizationMedicationInformation imi " +
            "   LEFT JOIN FETCH i.reactionObservation ro " +
            "   LEFT JOIN FETCH i.immunizationRefusalReason irr " +
            "   LEFT JOIN FETCH i.instructions instructions " +
            "   LEFT JOIN FETCH i.site site " +
            "   LEFT JOIN FETCH i.route route " +
            "WHERE i.id IN (" +
            "SELECT min(i.id) FROM Immunization i " +
            "   INNER JOIN i.immunizationMedicationInformation imi " +
            "WHERE i.resident.id IN :residentIds " +
            "GROUP BY imi.text, i.immunizationStopped, i.immunizationStarted, i.statusCode)",
            countQuery = "SELECT count(i) FROM Immunization i " +
                    "WHERE i.id IN (" +
                    "SELECT min(i.id) FROM Immunization i " +
                    "   INNER JOIN i.immunizationMedicationInformation imi " +
                    "WHERE i.resident.id IN :residentIds " +
                    "GROUP BY imi.text, i.immunizationStopped, i.immunizationStarted, i.statusCode)")
    Page<Immunization> listResidentImmunizationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                  final Pageable pageable);

    @Query("SELECT count(i) FROM Immunization i " +
            "WHERE i.id IN (" +
            "SELECT min(i.id) FROM Immunization i " +
            "   INNER JOIN i.immunizationMedicationInformation imi " +
            "WHERE i.resident.id IN :residentIds " +
            "GROUP BY imi.text, i.immunizationStopped, i.immunizationStarted, i.statusCode)")
    Long countResidentImmunizationsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);

}
