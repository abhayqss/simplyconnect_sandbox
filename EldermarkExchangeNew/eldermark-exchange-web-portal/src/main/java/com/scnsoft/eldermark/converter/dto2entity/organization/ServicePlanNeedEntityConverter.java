package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dao.ProgramSubTypeDao;
import com.scnsoft.eldermark.dao.ProgramTypeDao;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanNeedDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedPriority;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ServicePlanNeedEntityConverter<T extends ServicePlanNeed> {

    @Autowired
    private ProgramTypeDao programTypeDao;

    @Autowired
    private ProgramSubTypeDao programSubTypeDao;

    protected T setCommonFields(ServicePlanNeedDto source, T target) {
        //id will be used to resolve chain id for ServicePlanNeed and new entity will be created. ServicePlan is Auditable
        target.setId(source.getId());
        target.setDomain(ServicePlanNeedType.findByDomainId(source.getDomainId()));
        target.setPriority(ServicePlanNeedPriority.findByPriorityId(source.getPriorityId()));
        if (source.getProgramTypeId() != null) {
            target.setProgramType(programTypeDao.findById(source.getProgramTypeId()).orElse(null));
        }
        if (source.getProgramSubTypeId() != null) {
            target.setProgramSubType(programSubTypeDao.findById(source.getProgramSubTypeId()).orElse(null));
        }
        return target;
    }
}
