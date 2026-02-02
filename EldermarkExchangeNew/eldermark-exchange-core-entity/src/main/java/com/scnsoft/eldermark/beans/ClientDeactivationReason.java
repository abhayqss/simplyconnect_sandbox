package com.scnsoft.eldermark.beans;

import java.util.Arrays;

public enum ClientDeactivationReason {
    COMPLETED_PROGRAM("Completed Program"),
    DECLINED_SERVICES("Declined Services"),
    GRADUATED("Graduated"),
    NO_CONTACT("No Contact"),
    LIVES_OUTSIDE_SERVICE_AREA("Lives Outside Service Area"),
    DECEASED("Deceased"),
    CRIMINAL_ACTIVITY("Criminal Activity/ Destruction of Property/ Violence"),
    DISAGREEMENT_WITH_RULES_PERSONS("Disagreement with Rules/Persons"),
    LEFT_FOR_HOUSING_OPPORTUNITY("Left for Housing Opportunity"),
    NEEDS_COULD_NOT_BE_MET("Needs Could Not be Met"),
    NON_COMPLIANCE("Non-Compliance with Program"),
    NON_PAYMENT("Non-Payment of rent/ occupancy"),
    REACHED_MAXIMUM("Reached Maximum Time Allowed by program"),
    UNKNOWN_DISAPPEAR("Unknown/Disappeared"),
    OTHER("Other");


    private final String title;

    ClientDeactivationReason(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ClientDeactivationReason fromValue(String value) {
        return Arrays.stream(ClientDeactivationReason.values())
            .filter(reason -> reason.name().equals(value))
            .findFirst()
            .orElse(null);
    }
}
