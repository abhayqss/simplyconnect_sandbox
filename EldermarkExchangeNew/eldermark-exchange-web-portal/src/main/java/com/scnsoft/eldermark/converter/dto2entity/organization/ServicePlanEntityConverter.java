package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.serviceplan.ServicePlanDto;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanNeedDto;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.ServicePlanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class ServicePlanEntityConverter implements Converter<ServicePlanDto, ServicePlan> {

    @Autowired
    private Converter<ServicePlanNeedDto, ServicePlanGoalNeed> servicePlanGoalNeedEntityConverter;

    @Autowired
    private Converter<ServicePlanNeedDto, ServicePlanEducationNeed> servicePlanEducationNeedEntityConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ServicePlanService servicePlanService;

    //persistent entity should not be the result of this method because ServicePlan
    //is Auditable and setting id to null on persistent entity causes exception
    @Override
    public ServicePlan convert(ServicePlanDto source) {

        //todo check dates timezone shifts
        ServicePlan target = Optional.ofNullable(source.getId())
                .map(servicePlanService::findById)
                .map(this::copyNonEditableFields)
                .orElseGet(() -> {
                    var sp = new ServicePlan();
                    sp.setClient(clientService.getById(source.getClientId()));
                    sp.setDateCreated(Instant.ofEpochMilli(source.getDateCreated()));
                    return sp;
                });

        if (source.getIsCompleted() && !ServicePlanStatus.SHARED_WITH_CLIENT.equals(target.getServicePlanStatus())) {
            target.setDateCompleted(Instant.now());
            target.setServicePlanStatus(ServicePlanStatus.SHARED_WITH_CLIENT);
        } else {
            target.setDateCompleted(null);
            target.setServicePlanStatus(ServicePlanStatus.IN_DEVELOPMENT);
        }

        target.setScoring(new ServicePlanScoring());
        target.getScoring().setServicePlan(target);
        updateScoring(target.getScoring(), source);

        List<ServicePlanNeed> servicePlanNeedList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(source.getNeeds())) {
            for (ServicePlanNeedDto needDto : source.getNeeds()) {
                ServicePlanNeed need = ServicePlanNeedType.EDUCATION_TASK.getDomainNumber().equals(needDto.getDomainId()) ?
                        servicePlanEducationNeedEntityConverter.convert(needDto) :
                        servicePlanGoalNeedEntityConverter.convert(needDto);
                need.setServicePlan(target);

                servicePlanNeedList.add(need);
            }
        }

        target.setNeeds(servicePlanNeedList);
        target.setEmployee(loggedUserService.getCurrentEmployee());
        return target;
    }

    private ServicePlan copyNonEditableFields(ServicePlan servicePlan) {
        var sp = new ServicePlan();

        sp.setId(servicePlan.getId());
        sp.setChainId(servicePlan.getChainId());
        sp.setAuditableStatus(servicePlan.getAuditableStatus());

        sp.setClient(servicePlan.getClient());
        sp.setDateCreated(servicePlan.getDateCreated());
        return sp;
    }

    private void updateScoring(ServicePlanScoring scoring, ServicePlanDto servicePlanDto) {
        if (CollectionUtils.isNotEmpty(servicePlanDto.getScoring())) {
            servicePlanDto.getScoring().stream().forEach(scoreDto -> {
                ServicePlanUtils.domainScoringSetter(scoring, ServicePlanNeedType.findByDomainId(scoreDto.getDomainId()))
                        .ifPresent(setter -> setter.accept(scoreDto.getScore()));
            });
        }
    }

}
