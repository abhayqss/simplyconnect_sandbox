package com.scnsoft.eldermark.beans;

import java.util.Arrays;

public enum ProspectDeactivationReason {
    DECLINED_SERVICES("Declined services"),
    NO_CONTACT("No contact"),
    DECEASED("Deceased"),
    CRIMINAL_ACTIVITY("Criminal activity/ Destruction of property/ Violence"),
    DISAGREEMENT("Disagreement with rules/persons"),
    CHOSE_DIFFERENT_COMMUNITY("Chose different community"),
    NEEDS_COULD_NOT_BE_MET("Needs could not be met"),
    OTHER("Other");

    private final String title;

    ProspectDeactivationReason(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ProspectDeactivationReason fromValue(String value) {
        return Arrays.stream(ProspectDeactivationReason.values())
                .filter(reason -> reason.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}
