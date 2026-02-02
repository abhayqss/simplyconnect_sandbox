package com.scnsoft.eldermark.consana.sync.client.model.queue;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConsanaPatientUpdateType {
    PATIENT_UPDATE("PatientUpdate");

    private final String strValue;

    ConsanaPatientUpdateType(String strValue) {
        this.strValue = strValue;
    }

    @JsonValue
    public String getStrValue() {
        return strValue;
    }
}
