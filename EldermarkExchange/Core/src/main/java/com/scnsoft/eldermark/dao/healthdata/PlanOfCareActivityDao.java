package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PlanOfCareActivityDao extends JpaRepository<PlanOfCareActivity, Long> {
    @Query(value = "SELECT poca FROM PlanOfCareActivity poca WHERE poca.id IN " +
            "           (SELECT MIN(pocact.id) FROM PlanOfCareActivity pocact " +
            "               where pocact.id in " +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfAct p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfEncounter p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfInstruction p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfObservation p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfProcedure p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfSubstanceAdministration p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfSupply p where p.resident.id in :residentIds)" +
            " GROUP BY pocact.code.displayName, pocact.effectiveTime)")
    Page<PlanOfCareActivity> listResidentPlanOfCareActivityWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds, final Pageable pageable);

    @Query(value = "SELECT count(poca) FROM PlanOfCareActivity poca WHERE poca.id IN " +
            "           (SELECT MIN(pocact.id) FROM PlanOfCareActivity pocact " +
            "               where pocact.id in " +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfAct p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfEncounter p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfInstruction p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfObservation p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfProcedure p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfSubstanceAdministration p where p.resident.id in :residentIds)" +
            "               or pocact.id in" +
            "                   (SELECT a.id from PlanOfCareActivity a join a.planOfCareIfSupply p where p.resident.id in :residentIds)" +
            " GROUP BY pocact.code.displayName, pocact.effectiveTime)")
    Long countResidentPlanOfCareActivityWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
