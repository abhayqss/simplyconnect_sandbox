package com.scnsoft.eldermark.dump.model;

import java.util.Map;

public class MedicalDiagnosisInfo {
    private String clientName;
    private Map<MedicalDiagnosisField, String> medicalDiagnosisFields;

    public MedicalDiagnosisInfo(String clientName, Map<MedicalDiagnosisField, String> medicalDiagnosisFields) {
        this.clientName = clientName;
        this.medicalDiagnosisFields = medicalDiagnosisFields;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Map<MedicalDiagnosisField, String> getMedicalDiagnosisFields() {
        return medicalDiagnosisFields;
    }

    public void setMedicalDiagnosisFields(Map<MedicalDiagnosisField, String> medicalDiagnosisFields) {
        this.medicalDiagnosisFields = medicalDiagnosisFields;
    }
}
