package com.scnsoft.eldermark.entity.serviceplan;

import java.util.*;

public enum ServicePlanNeedType {
    HEALTH_STATUS("Health Status", 1L),
    TRANSPORTATION("Transportation", 2L),
    HOUSING("Housing / Home Security & Safety", 3L),
    NUTRITION_SECURITY("Nutrition Security", 4L),
    SUPPORT("Caregiver Resource / Support", 5L),
    BEHAVIORAL("Behavioral / Spiritual Health", 6L),
    OTHER("Other / Non-Specific", 7L),
    EDUCATION_TASK("Relevant Activation or Education Task", 8L),
    HOUSING_ONLY("Housing", 9L),
    SOCIAL_WELLNESS("Social wellness", 10L),
    EMPLOYMENT("Employment", 11L),
    MENTAL_WELLNESS("Mental wellness", 12L),
    PHYSICAL_WELLNESS("Physical wellness", 13L),
    LEGAL("Legal", 14L),
    FINANCES("Finances", 15L),
    MEDICAL_OTHER_SUPPLY("Medical / Other Supply", 16L),
    MEDICATION_MGMT_ASSISTANCE("Medication Management and Assistance", 17L),
    HOME_HEALTH("Home Health", 18L);

    private String displayName;
    private Long domainNumber;

    private static final Map<String, ServicePlanNeedType> displayNameEnumMap;
    private static final Map<Long, ServicePlanNeedType> displayIdEnumMap;

    static {
        displayNameEnumMap = new HashMap<>();
        for (ServicePlanNeedType type : ServicePlanNeedType.values()) {
            displayNameEnumMap.put(type.displayName, type);
        }
        displayIdEnumMap = new HashMap<>();
        for (ServicePlanNeedType type : ServicePlanNeedType.values()) {
            displayIdEnumMap.put(type.domainNumber, type);
        }
    }

    public static ServicePlanNeedType findByDisplayName(String displayName) {
        return displayNameEnumMap.get(displayName);
    }

    public static ServicePlanNeedType findByDomainId(Long domainId) {
        return displayIdEnumMap.get(domainId);
    }

    ServicePlanNeedType(String displayName, Long domainNumber) {
        this.displayName = displayName;
        this.domainNumber = domainNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getDomainNumber() {
        return domainNumber;
    }
}
