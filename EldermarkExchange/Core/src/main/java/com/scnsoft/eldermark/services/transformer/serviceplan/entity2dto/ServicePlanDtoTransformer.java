package com.scnsoft.eldermark.services.transformer.serviceplan.entity2dto;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoring;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.NeedDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanDtoTransformer extends ListAndItemTransformer<ServicePlan, ServicePlanDto> {

    @Autowired
    private ListAndItemTransformer<ServicePlanNeed, NeedDto> servicePlanNeedNeedDtoTransformer;

    @Override
    public ServicePlanDto convert(ServicePlan servicePlan) {
        if (servicePlan == null) {
            return null;
        }
        ServicePlanDto servicePlanDto = new ServicePlanDto();
        servicePlanDto.setId(servicePlan.getId());
        servicePlanDto.setChainId(servicePlan.getChainId());
        servicePlanDto.setDateCompleted(servicePlan.getDateCompleted());
        servicePlanDto.setDateCreated(servicePlan.getDateCreated());
        servicePlanDto.setCompleted(servicePlan.getDateCompleted() != null);
        servicePlanDto.setCreatedById(servicePlan.getEmployee().getId());
        servicePlanDto.setCreatedBy(servicePlan.getEmployee().getFullName());

        ServicePlanScoring servicePlanScoring = servicePlan.getScoring();
        if (servicePlanScoring != null) {
            servicePlanDto.setServicePlanScoringId(servicePlanScoring.getId());
            servicePlanDto.setHealthStatusScore(servicePlanScoring.getHealthStatusScore());
            servicePlanDto.setTransportationScore(servicePlanScoring.getTransportationScore());
            servicePlanDto.setHousingScore(servicePlanScoring.getHousingScore());
            servicePlanDto.setNutritionSecurityScore(servicePlanScoring.getNutritionSecurityScore());
            servicePlanDto.setSupportScore(servicePlanScoring.getSupportScore());
            servicePlanDto.setBehavioralScore(servicePlanScoring.getBehavioralScore());
            servicePlanDto.setOtherScore(servicePlanScoring.getOtherScore());
            servicePlanDto.setHousingOnlyScore(servicePlanScoring.getHousingOnlyScore());
            servicePlanDto.setSocialWellnessScore(servicePlanScoring.getSocialWellnessScore());
            servicePlanDto.setMentalWellnessScore(servicePlanScoring.getMentalWellnessScore());
            servicePlanDto.setPhysicalWellnessScore(servicePlanScoring.getPhysicalWellness());
            servicePlanDto.setTaskScore(servicePlanScoring.getTaskScore());
            servicePlanDto.setEmploymentScore(servicePlanScoring.getEmploymentScore());
            servicePlanDto.setLegalScore(servicePlanScoring.getLegalScore());
            servicePlanDto.setFinancesScore(servicePlanScoring.getFinancesScore());
            servicePlanDto.setMedicalOtherSupplyScore(servicePlanScoring.getMedicalOtherSupplyScore());
            servicePlanDto.setMedicationMgmtAssistanceScore(servicePlanScoring.getMedicationMgmtAssistanceScore());
            servicePlanDto.setHomeHealthScore(servicePlanScoring.getHomeHealthScore());
        }

        if (CollectionUtils.isNotEmpty(servicePlan.getNeeds())) {
            servicePlanDto.setNeeds(servicePlanNeedNeedDtoTransformer.convertList(servicePlan.getNeeds()));
        }
        return servicePlanDto;
    }

}
