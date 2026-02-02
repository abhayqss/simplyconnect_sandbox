package com.scnsoft.eldermark.dump.entity.serviceplan;

import java.util.HashMap;
import java.util.Map;

public enum ServicePlanNeedType {
    BEHAVIORAL("Behavioral / Spiritual Health",6L),
    SUPPORT("Caregiver Resource / Support",5L),
    HEALTH_STATUS("Health Status",1L),
    HOUSING("Housing / Home Security & Safety",3L),
    NUTRITION_SECURITY("Nutrition Security",4L),
    EDUCATION_TASK("Relevant Activation or Education Task",8L),
    TRANSPORTATION("Transportation",2L),
    OTHER("Other / Non-Specific",7L),
    HOUSING_ONLY("Housing", 9L),
    SOCIAL_WELLNESS("Social wellness", 10L),
    EMPLOYMENT("Employment", 11L),
    MENTAL_WELLNESS("Mental wellness", 12L),
    PHYSICAL_WELLNESS("Physical wellness", 13L);

    private String displayName;
    private Long numberDomain;

    private static final Map<String,ServicePlanNeedType> displayNameEnumMap;
    private static final Map<Long,ServicePlanNeedType> displayIdEnumMap;

    static {
        displayNameEnumMap = new HashMap<>();
        for (ServicePlanNeedType type : ServicePlanNeedType.values()) {
            displayNameEnumMap.put(type.displayName, type);
        }
        displayIdEnumMap = new HashMap<>();
        for (ServicePlanNeedType type : ServicePlanNeedType.values()) {
            displayIdEnumMap.put(type.numberDomain, type);
        }
    }

    public static ServicePlanNeedType findByDisplayName(String displayName) {
        return displayNameEnumMap.get(displayName);
    }
    
    public static ServicePlanNeedType findByDomainId(Long domainId) {
        return displayIdEnumMap.get(domainId);
    }

    ServicePlanNeedType(String displayName, Long numberDomain) {
        this.displayName = displayName;
        this.numberDomain = numberDomain;
    }

    public String getDisplayName() {
        return displayName;
    }

	public Long getNumberDomain() {
		return numberDomain;
	}
}
