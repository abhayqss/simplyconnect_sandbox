package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.serviceplan.ProgramSubTypeToServiceTreatmentApproach;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedTypeToServiceTreatmentApproach;

import java.util.List;

public interface MappingDataToServiceTreatmentApproachService {

    List<ProgramSubTypeToServiceTreatmentApproach> findByProgramSubType(Long programSubTypeId);

    List<ServicePlanNeedTypeToServiceTreatmentApproach> findByServicePlanNeedType(ServicePlanNeedType servicePlanNeedType);
}
