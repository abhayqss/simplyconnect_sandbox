package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.ResidentAssessmentResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Repository
//public interface ResidentAssessmentResultDao extends JpaRepository<ResidentAssessmentResult, Long>, JpaSpecificationExecutor<ResidentAssessmentResult> {
public interface ResidentAssessmentResultDao extends ResidentAwareAuditableEntityDao<ResidentAssessmentResult> {
    Sort.Order ORDER_BY_DATE_COMPLETED = new Sort.Order(DESC, "dateCompleted");
    //Long countByResident_IdInAndArchivedIsFalse(Collection<Long> residentIds);
    //Page<ResidentAssessmentResult> getAllByResident_IdInAndArchivedIsFalse(Collection<Long> residentIds, Pageable pageable);

    @Query("Select distinct assessment from ResidentAssessmentResult assessment " +
            "where assessment.resident.id in (:residentIds) and assessment.archived=false and (assessment.assessment.name like :firstSearchPart " +
            "or assessment.employee.firstName like :firstSearchPart " +
            "or assessment.employee.lastName like :firstSearchPart " +
            "or (assessment.employee.firstName like :firstSearchPart and assessment.employee.lastName like :secondSearchPart) " +
            "or (assessment.employee.firstName like :secondSearchPart and assessment.employee.lastName like :firstSearchPart)) ")
    Page<ResidentAssessmentResult> getAllByResident_IdInAndArchivedIsFalseWithSearch(@Param("residentIds") Collection<Long> residentIds, @Param("firstSearchPart") String firstSearchPart, @Param("secondSearchPart") String secondSearchPart, Pageable pageable);

    @Query("Select distinct assessment from ResidentAssessmentResult assessment left join assessment.assessment.databases dbs " +
            "where assessment.resident.id in (:residentIds) and assessment.archived=false and (dbs.id is null or dbs.id=assessment.resident.databaseId) " +
            "and (assessment.assessment.name like :firstSearchPart " +
            "or assessment.employee.firstName like :firstSearchPart " +
            "or assessment.employee.lastName like :firstSearchPart " +
            "or (assessment.employee.firstName like :firstSearchPart and assessment.employee.lastName like :secondSearchPart) " +
            "or (assessment.employee.firstName like :secondSearchPart and assessment.employee.lastName like :firstSearchPart)) ")
    Page<ResidentAssessmentResult> getAllByResident_IdInAndArchivedIsFalseWithSearchForAffiliated(@Param("residentIds") Collection<Long> residentIds, @Param("firstSearchPart") String firstSearchPart, @Param("secondSearchPart") String secondSearchPart, Pageable pageable);

    @Query("Select distinct assessment from ResidentAssessmentResult assessment left join assessment.assessment.databases dbs " +
            "where assessment.resident.id in (:residentIds) and assessment.archived=false and (dbs.id is null or dbs.id=assessment.resident.databaseId) ")
    Page<ResidentAssessmentResult> getAllByResident_IdInAndArchivedIsFalseForAffiliated(@Param("residentIds") Collection<Long> residentIds, Pageable pageable);

    @Query("Select count(ras) from ResidentAssessmentResult ras left join ras.assessment.databases dbs " +
            "where ras.resident.id in (:residentIds) and ras.archived = false and (dbs.id is null or dbs.id=ras.resident.databaseId)  ")
    Long countResidentAssessmentsForAffiliated(@Param("residentIds") Collection<Long> residentIds);

    @Query("select distinct ras from ResidentAssessmentResult ras " +
            "where ras.assessment.code = :assessmentCode " +
            "and ras.archived = false and ras.resident.id = :residentId " +
            "order by ras.dateAssigned desc "
    )
    List<ResidentAssessmentResult> getComprehensiveAssessmentsOfResident(@Param("residentId") Long residentId, @Param("assessmentCode") String assessmentCode);
}
