package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.AssessmentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentGroupDao extends JpaRepository<AssessmentGroup, Long>, JpaSpecificationExecutor<AssessmentGroup> {

    @Query("Select distinct ag from AssessmentGroup ag left join ag.assessments asm left join asm.databases dbs " +
            "where dbs.id is null or dbs.id=:patientDatabaseId ")
    List<AssessmentGroup> findAccessibleAssessmentGroupsForPatientDatabase(@Param("patientDatabaseId") Long patientDatabaseId);
}
