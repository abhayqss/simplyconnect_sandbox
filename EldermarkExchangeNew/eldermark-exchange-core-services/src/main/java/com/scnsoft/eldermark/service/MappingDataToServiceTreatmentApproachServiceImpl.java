package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ProgramSubTypeToServiceTreatmentApproachDao;
import com.scnsoft.eldermark.dao.ServicePlanNeedTypeToServiceTreatmentApproachDao;
import com.scnsoft.eldermark.entity.serviceplan.ProgramSubTypeToServiceTreatmentApproach;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedTypeToServiceTreatmentApproach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MappingDataToServiceTreatmentApproachServiceImpl implements MappingDataToServiceTreatmentApproachService {

    @Autowired
    private ProgramSubTypeToServiceTreatmentApproachDao programSubTypeToServiceTreatmentApproachDao;

    @Autowired
    private ServicePlanNeedTypeToServiceTreatmentApproachDao servicePlanNeedTypeToServiceTreatmentApproachDao;

    @Override
    public List<ProgramSubTypeToServiceTreatmentApproach> findByProgramSubType(Long programSubTypeId) {
        return programSubTypeToServiceTreatmentApproachDao.findAllByProgramSubTypeId(programSubTypeId);
    }

    @Override
    public List<ServicePlanNeedTypeToServiceTreatmentApproach> findByServicePlanNeedType(ServicePlanNeedType servicePlanNeedType) {
        return servicePlanNeedTypeToServiceTreatmentApproachDao.findAllByServicePlanNeedType(servicePlanNeedType);
    }
}
