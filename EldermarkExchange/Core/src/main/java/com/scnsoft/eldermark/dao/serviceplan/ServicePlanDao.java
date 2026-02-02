package com.scnsoft.eldermark.dao.serviceplan;

import com.scnsoft.eldermark.dao.carecoordination.ResidentAwareAuditableEntityDao;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Repository
public interface ServicePlanDao extends ResidentAwareAuditableEntityDao<ServicePlan> {

    Sort.Order ORDER_BY_DATE_CREATED = new Sort.Order(DESC, "dateCreated");

    @Query("Select servicePlan from ServicePlan servicePlan where servicePlan.resident.id in (:residentIds) and servicePlan.archived = false and " +
            "servicePlan.scoring.totalScore = (:score)")
    Page<ServicePlan> getAllByResidentIdAndTotalScore(@Param("residentIds") Collection<Long> residentIds, @Param("score") Integer score, Pageable pageRequest);

    @Query("Select servicePlan from ServicePlan servicePlan where servicePlan.resident.id in (:residentIds) and servicePlan.archived = false and " +
            "((YEAR(servicePlan.dateCompleted) = YEAR(:searchDate) and MONTH(servicePlan.dateCompleted) = MONTH(:searchDate) and DAY(servicePlan.dateCompleted) = DAY(:searchDate)) " +
            "or (YEAR(servicePlan.dateCreated) = YEAR(:searchDate) and MONTH(servicePlan.dateCreated) = MONTH(:searchDate) and DAY(servicePlan.dateCreated) = DAY(:searchDate)))")
    Page<ServicePlan> getAllByResidentIdWithSearchByDate(@Param("residentIds") Collection<Long> residentIds, @Param("searchDate") Date searchDate, Pageable pageRequest);


    @Query("Select servicePlan from ServicePlan servicePlan " +
            "where servicePlan.resident.id in (:residentIds) and servicePlan.archived = false and (" +
            " servicePlan.employee.firstName like :firstSearchPart " +
            "or servicePlan.employee.lastName like :firstSearchPart " +
            "or (servicePlan.employee.firstName like :firstSearchPart and servicePlan.employee.lastName like :secondSearchPart) " +
            "or (servicePlan.employee.firstName like :secondSearchPart and servicePlan.employee.lastName like :firstSearchPart)) ")
    Page<ServicePlan> getAllByResidentIdWithSearchByAuthorAndStatus(@Param("residentIds") Collection<Long> residentIds, @Param("firstSearchPart") String firstSearchPart,
                                                                    @Param("secondSearchPart") String secondSearchPart, Pageable pageRequest);

    @Query(value="SELECT sp FROM ServicePlan sp WHERE sp.id = :id or sp.chainId = :id")
    Page<ServicePlan> getServicePlanHistory(@Param("id") Long id, final Pageable pageable);

    Long countByResident_IdInAndServicePlanStatusAndArchivedIsFalse(Collection<Long> residentIds, ServicePlanStatus servicePlanStatus);

    @Query(value="SELECT distinct sp.id FROM ServicePlan sp WHERE sp.resident.id =:residentId and sp.employee.id in (:employeeIds) and sp.archived = false")
    Set<Long> getServicePlanIdsForPatientCreatedByEmployeeIds(@Param("residentId") Long residentId, @Param("employeeIds") Collection<Long> employeeIds);

    @Query(value="SELECT distinct sp.id FROM ServicePlan sp WHERE sp.resident.id =:residentId and sp.archived = false")
    Set<Long> getServicePlanIdsForPatient(@Param("residentId") Long residentId);
}
