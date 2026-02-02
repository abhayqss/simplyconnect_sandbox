package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.ProcedureActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProcedureActivityDao extends JpaRepository<ProcedureActivity, Long> {

    @Query(value = "SELECT pa FROM ProcedureActivity pa WHERE pa.id IN " +
            "           (SELECT MIN(ppa.id) FROM ProcedureActivity ppa " +
            "               where ppa.id in" +
            "                   (SELECT a.id FROM ProcedureActivity a join a.procedureIfProcedure p where p.resident.id in :residentIds)" +
            "               or ppa.id in " +
            "                   (SELECT a.id FROM ProcedureActivity a join a.procedureIfAct p where p.resident.id in :residentIds) " +
            "               or ppa.id in " +
            "                   (SELECT a.id FROM ProcedureActivity a join a.procedureIfObservation p where p.resident.id in :residentIds) " +
            "           GROUP BY ppa.procedureTypeText, ppa.procedureStarted, ppa.procedureStopped)"
    )


    Page<ProcedureActivity> listResidentProcedureActivitiesWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                             Pageable pageable);

    @Query(value = "SELECT count(pa) FROM ProcedureActivity pa WHERE pa.id IN " +
            "           (SELECT MIN(ppa.id) FROM ProcedureActivity ppa " +
            "               where ppa.id in" +
            "                   (SELECT a.id FROM ProcedureActivity a join a.procedureIfProcedure p where p.resident.id in :residentIds)" +
            "               or ppa.id in " +
            "                   (SELECT a.id FROM ProcedureActivity a join a.procedureIfAct p where p.resident.id in :residentIds) " +
            "               or ppa.id in " +
            "                   (SELECT a.id FROM ProcedureActivity a join a.procedureIfObservation p where p.resident.id in :residentIds) " +
            "           GROUP BY ppa.procedureTypeText, ppa.procedureStarted, ppa.procedureStopped)")
    Long countResidentProcedureActivitiesWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}