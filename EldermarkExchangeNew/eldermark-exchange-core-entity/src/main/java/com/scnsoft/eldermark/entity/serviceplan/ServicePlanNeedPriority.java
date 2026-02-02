package com.scnsoft.eldermark.entity.serviceplan;

import java.util.HashMap;
import java.util.Map;

public enum ServicePlanNeedPriority {
    LOW("Low", 1L), MEDIUM("Medium", 2L), HIGH("High", 3L);

    private String displayName;
    private Long numberPriority;

    private static final Map<String, ServicePlanNeedPriority> displayNameEnumMap;
    private static final Map<Long, ServicePlanNeedPriority> displayIdEnumMap;

    static {
        displayNameEnumMap = new HashMap<>();
        for (ServicePlanNeedPriority type : ServicePlanNeedPriority.values()) {
            displayNameEnumMap.put(type.displayName, type);
        }
        displayIdEnumMap = new HashMap<>();
        for (ServicePlanNeedPriority type : ServicePlanNeedPriority.values()) {
            displayIdEnumMap.put(type.numberPriority, type);
        }
    }

    public static ServicePlanNeedPriority findByDisplayName(String displayName) {
        return displayNameEnumMap.get(displayName);
    }

    public static ServicePlanNeedPriority findByPriorityId(Long priorityId) {
        return displayIdEnumMap.get(priorityId);
    }

    ServicePlanNeedPriority(String displayName, Long numberPriority) {
        this.displayName = displayName;
        this.numberPriority = numberPriority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getNumberPriority() {
        return numberPriority;
    }
}
