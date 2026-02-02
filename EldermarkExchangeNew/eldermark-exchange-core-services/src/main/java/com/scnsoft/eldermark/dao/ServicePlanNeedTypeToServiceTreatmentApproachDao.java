package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedTypeToServiceTreatmentApproach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicePlanNeedTypeToServiceTreatmentApproachDao extends JpaRepository<ServicePlanNeedTypeToServiceTreatmentApproach, Long> {

    List<ServicePlanNeedTypeToServiceTreatmentApproach> findAllByServicePlanNeedType(ServicePlanNeedType servicePlanNeedType);
}
