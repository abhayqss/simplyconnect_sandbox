package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.dto.notes.ServiceStatusCheckDto;
import com.scnsoft.eldermark.entity.note.ServiceStatusCheckNote;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ServiceStatusCheckNoteDtoToEntityConverter extends BaseNoteDtoToEntityConverter<ServiceStatusCheckNote> {

    @Autowired
    private ServicePlanService servicePlanService;

    @Override
    public ServiceStatusCheckNote convert(NoteDto source) {
        ServiceStatusCheckDto serviceStatusCheckDto = source.getServiceStatusCheck();
        ServiceStatusCheckNote target = new ServiceStatusCheckNote();
        convertBase(source, target);
        target.setServicePlan(servicePlanService.findById(serviceStatusCheckDto.getServicePlanId()));
        target.setResourceName(serviceStatusCheckDto.getResourceName());
        target.setProviderName(serviceStatusCheckDto.getProviderName());
        target.setAuditPerson(serviceStatusCheckDto.getAuditPerson());
        target.setCheckDate(DateTimeUtils.toInstant(serviceStatusCheckDto.getCheckDate()));
        target.setNextCheckDate(DateTimeUtils.toInstant(serviceStatusCheckDto.getNextCheckDate()));
        target.setServiceProvided(serviceStatusCheckDto.getServiceProvided());
        return target;
    }
}
