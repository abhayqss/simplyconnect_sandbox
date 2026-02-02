package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan_;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicePlanDao extends AppJpaRepository<ServicePlan, Long>, CustomServicePlanDao {

    Sort SORT_BY_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, ServicePlan_.DATE_CREATED);
    Sort SORT_BY_DATE_COMPLETED_DESC_NULLS_FIRST = Sort.by(new Sort.Order(Sort.Direction.DESC, ServicePlan_.DATE_COMPLETED, Sort.NullHandling.NULLS_FIRST));

    boolean existsByClientIdAndArchivedIsFalseAndServicePlanStatus(Long clientId,
                                                                   ServicePlanStatus servicePlanStatus);

    boolean existsByClientIdAndArchivedIsFalseAndServicePlanStatusAndIdNot(Long clientId,
                                                                           ServicePlanStatus servicePlanStatus,
                                                                           Long servicePlanId);

}
