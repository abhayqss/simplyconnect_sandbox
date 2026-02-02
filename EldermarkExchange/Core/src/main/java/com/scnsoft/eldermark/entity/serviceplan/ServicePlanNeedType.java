package com.scnsoft.eldermark.entity.serviceplan;

import java.util.HashMap;
import java.util.Map;

public enum ServicePlanNeedType {
    HEALTH_STATUS("Health Status"),
    TRANSPORTATION("Transportation"),
    HOUSING("Housing / Home Security & Safety"),
    NUTRITION_SECURITY("Nutrition Security"),
    SUPPORT("Caregiver Resource / Support"),
    BEHAVIORAL("Behavioral / Spiritual Health"),
    OTHER("Other / Non-Specific"),
    EDUCATION_TASK("Relevant Activation or Education Task"),
    HOUSING_ONLY("Housing"),
    SOCIAL_WELLNESS("Social wellness"),
    EMPLOYMENT("Employment"),
    MENTAL_WELLNESS("Mental wellness"),
    PHYSICAL_WELLNESS("Physical wellness"),
    LEGAL("Legal"),
    FINANCES("Finances"),
    MEDICAL_OTHER_SUPPLY("Medical / Other Supply"),
    MEDICATION_MGMT_ASSISTANCE("Medication Management and Assistance"),
    HOME_HEALTH("Home Health");

    private String displayName;

    private static final Map<String,ServicePlanNeedType> displayNameEnumMap;

    static {
        displayNameEnumMap = new HashMap<>();
        for (ServicePlanNeedType type : ServicePlanNeedType.values()) {
            displayNameEnumMap.put(type.displayName, type);
        }
    }

    public static ServicePlanNeedType findByDisplayName(String displayName) {
        return displayNameEnumMap.get(displayName);
    }

    ServicePlanNeedType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
