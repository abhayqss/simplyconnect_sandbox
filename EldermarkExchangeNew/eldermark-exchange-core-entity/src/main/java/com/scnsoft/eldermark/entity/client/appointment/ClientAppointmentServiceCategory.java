package com.scnsoft.eldermark.entity.client.appointment;

public enum ClientAppointmentServiceCategory {

    PREVENTATIVE_CARE("Preventative Care"),
    ORAL_CARE("Oral Care"),
    EYE_CARE("Eye Care"),
    LEGAL("Legal"),
    FAMILY("Family"),
    HOMECARE_SERVICES("Homecare Services"),
    OUT_OF_COMMUNITY("Out of Community"),
    HOSPICE("Hospice"),
    SPIRITUAL("Spiritual"),
    OTHER("Other");


    private String displayName;

    ClientAppointmentServiceCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
