package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.entity.serviceplan.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServicePlanUtils {


    public static Optional<Consumer<Integer>> domainScoringSetter(ServicePlanScoring scoring, ServicePlanNeedType domain) {
        switch (domain) {
            case BEHAVIORAL:
                return Optional.of(scoring::setBehavioralScore);
            case SUPPORT:
                return Optional.of(scoring::setSupportScore);
            case HEALTH_STATUS:
                return Optional.of(scoring::setHealthStatusScore);
            case HOUSING:
                return Optional.of(scoring::setHousingScore);
            case NUTRITION_SECURITY:
                return Optional.of(scoring::setNutritionSecurityScore);
            case TRANSPORTATION:
                return Optional.of(scoring::setTransportationScore);
            case OTHER:
                return Optional.of(scoring::setOtherScore);
            case HOUSING_ONLY:
                return Optional.of(scoring::setHousingOnlyScore);
            case SOCIAL_WELLNESS:
                return Optional.of(scoring::setSocialWellnessScore);
            case EMPLOYMENT:
                return Optional.of(scoring::setEmploymentScore);
            case MENTAL_WELLNESS:
                return Optional.of(scoring::setMentalWellnessScore);
            case PHYSICAL_WELLNESS:
                return Optional.of(scoring::setPhysicalWellnessScore);
            case LEGAL:
                return Optional.of(scoring::setLegalScore);
            case FINANCES:
                return Optional.of(scoring::setFinancesScore);
            case MEDICAL_OTHER_SUPPLY:
                return Optional.of(scoring::setMedicalOtherSupplyScore);
            case MEDICATION_MGMT_ASSISTANCE:
                return Optional.of(scoring::setMedicationMgmtAssistanceScore);
            case HOME_HEALTH:
                return Optional.of(scoring::setHomeHealthScore);
            default:
                return Optional.empty();
        }
    }

    public static Optional<Supplier<Integer>> domainScoringGetter(ServicePlanScoringCalculable scoring, ServicePlanNeedType domain) {
        switch (domain) {
            case BEHAVIORAL:
                return Optional.of(scoring::getBehavioralScore);
            case SUPPORT:
                return Optional.of(scoring::getSupportScore);
            case HEALTH_STATUS:
                return Optional.of(scoring::getHealthStatusScore);
            case HOUSING:
                return Optional.of(scoring::getHousingScore);
            case NUTRITION_SECURITY:
                return Optional.of(scoring::getNutritionSecurityScore);
            case TRANSPORTATION:
                return Optional.of(scoring::getTransportationScore);
            case OTHER:
                return Optional.of(scoring::getOtherScore);
            case HOUSING_ONLY:
                return Optional.of(scoring::getHousingOnlyScore);
            case SOCIAL_WELLNESS:
                return Optional.of(scoring::getSocialWellnessScore);
            case EMPLOYMENT:
                return Optional.of(scoring::getEmploymentScore);
            case MENTAL_WELLNESS:
                return Optional.of(scoring::getMentalWellnessScore);
            case PHYSICAL_WELLNESS:
                return Optional.of(scoring::getPhysicalWellnessScore);
            case LEGAL:
                return Optional.of(scoring::getLegalScore);
            case FINANCES:
                return Optional.of(scoring::getFinancesScore);
            case MEDICAL_OTHER_SUPPLY:
                return Optional.of(scoring::getMedicalOtherSupplyScore);
            case MEDICATION_MGMT_ASSISTANCE:
                return Optional.of(scoring::getMedicationMgmtAssistanceScore);
            case HOME_HEALTH:
                return Optional.of(scoring::getHomeHealthScore);
            default:
                return Optional.empty();
        }
    }

    public static Integer resolveScore(ServicePlanScoringCalculable scoring, ServicePlanNeedType domain) {
        if (scoring == null) {
            return null;
        }
        //or it is enough to return zero always?
        return domainScoringGetter(scoring, domain)
                .map(getter -> Optional.ofNullable(getter.get()).orElse(0))
                .orElse(null);
    }

    public static List<ServicePlanGoal> getGoals(ServicePlan servicePlan) {
        if (servicePlan == null) {
            return Collections.emptyList();
        }
        return CollectionUtils.emptyIfNull(servicePlan.getNeeds()).stream()
                .filter(need -> need instanceof ServicePlanGoalNeed)
                .map(need -> (ServicePlanGoalNeed) need)
                .map(ServicePlanGoalNeed::getGoals)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
