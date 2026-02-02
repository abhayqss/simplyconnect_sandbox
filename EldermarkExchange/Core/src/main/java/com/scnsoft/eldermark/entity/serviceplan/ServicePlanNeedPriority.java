package com.scnsoft.eldermark.entity.serviceplan;

import java.util.HashMap;
import java.util.Map;

public enum ServicePlanNeedPriority {
    LOW("Low",1),
    MEDIUM("Medium",2),
    HIGH("High",3);

    private String displayName;
    private Integer numberPriority;

    private static final Map<String,ServicePlanNeedPriority> displayNameEnumMap;

    static {
        displayNameEnumMap = new HashMap<>();
        for (ServicePlanNeedPriority type : ServicePlanNeedPriority.values()) {
            displayNameEnumMap.put(type.displayName, type);
        }
    }

    public static ServicePlanNeedPriority findByDisplayName(String displayName) {
        return displayNameEnumMap.get(displayName);
    }

    ServicePlanNeedPriority(String displayName, Integer numberPriority) {
        this.displayName = displayName;
        this.numberPriority = numberPriority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getNumberPriority() {
        return numberPriority;
    }
}
