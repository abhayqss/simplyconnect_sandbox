package com.scnsoft.eldermark.services.transformer.serviceplan.dto2entity;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoring;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanDto;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanScoringEntityTransformer extends ListAndItemTransformer<ServicePlanDto, ServicePlanScoring> {
    @Override
    public ServicePlanScoring convert(ServicePlanDto servicePlanDto) {
        if (servicePlanDto == null) {
            return null;
        }
        ServicePlanScoring servicePlanScoring = new ServicePlanScoring();
        servicePlanScoring.setHealthStatusScore(servicePlanDto.getHealthStatusScore());
        servicePlanScoring.setTransportationScore(servicePlanDto.getTransportationScore());
        servicePlanScoring.setHousingScore(servicePlanDto.getHousingScore());
        servicePlanScoring.setNutritionSecurityScore(servicePlanDto.getNutritionSecurityScore());
        servicePlanScoring.setSupportScore(servicePlanDto.getSupportScore());
        servicePlanScoring.setBehavioralScore(servicePlanDto.getBehavioralScore());
        servicePlanScoring.setOtherScore(servicePlanDto.getOtherScore());
        servicePlanScoring.setHousingOnlyScore(servicePlanDto.getHousingOnlyScore());
        servicePlanScoring.setSocialWellnessScore(servicePlanDto.getSocialWellnessScore());
        servicePlanScoring.setMentalWellnessScore(servicePlanDto.getMentalWellnessScore());
        servicePlanScoring.setPhysicalWellness(servicePlanDto.getPhysicalWellnessScore());
        servicePlanScoring.setTaskScore(servicePlanDto.getTaskScore());
        servicePlanScoring.setEmploymentScore(servicePlanDto.getEmploymentScore());
        servicePlanScoring.setLegalScore(servicePlanDto.getLegalScore());
        servicePlanScoring.setFinancesScore(servicePlanDto.getFinancesScore());
        servicePlanScoring.setMedicalOtherSupplyScore(servicePlanDto.getMedicalOtherSupplyScore());
        servicePlanScoring.setMedicationMgmtAssistanceScore(servicePlanDto.getMedicationMgmtAssistanceScore());
        servicePlanScoring.setHomeHealthScore(servicePlanDto.getHomeHealthScore());
        return servicePlanScoring;
    }
}
