package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ServicePlanCount;
import com.scnsoft.eldermark.entity.serviceplan.ClientServicePlanScoringAware;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomServicePlanDao {

    List<ServicePlanCount> countGroupedByStatus(Specification<ServicePlan> specification);

    List<ClientServicePlanScoringAware> findAllClientAndEmployeeServicePlanScoring(Specification<ServicePlan> specification);
}
