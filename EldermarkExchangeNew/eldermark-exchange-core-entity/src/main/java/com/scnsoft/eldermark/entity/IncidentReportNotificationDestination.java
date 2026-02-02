package com.scnsoft.eldermark.entity;

public enum IncidentReportNotificationDestination {
    FAMILY("Family", true),
    FRIEND("Friend", true),
    PHYSICIAN("Physician", true),
    ADULT_PROTECTIVE_SERVICES("Adult Protective Services", false),
    CARE_MANAGER("Care Manager", true),
    OHIO_DEPARTMENT_OF_HEALTH("Ohio Department of Health", false),
    _9_1_1("9-1-1", false),
    POLICE("Police", false),
    OTHER("Other", false);

    private final String displayName;

    private final boolean hasPersonalInfo;

    IncidentReportNotificationDestination(String displayName, boolean hasPersonalInfo) {
        this.displayName = displayName;
        this.hasPersonalInfo = hasPersonalInfo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean hasPersonalInfo() {
        return hasPersonalInfo;
    }
}
