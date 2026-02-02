package com.scnsoft.eldermark.mobile.dto.medication;

import java.util.List;

public class MedicationSearchResultDto {

    private String mediSpanId;
    private String name;
    private String route;
    private String dosageForm;
    private String strength;
    private List<String> ndcCodes;

    public String getMediSpanId() {
        return mediSpanId;
    }

    public void setMediSpanId(String mediSpanId) {
        this.mediSpanId = mediSpanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public List<String> getNdcCodes() {
        return ndcCodes;
    }

    public void setNdcCodes(List<String> ndcCodes) {
        this.ndcCodes = ndcCodes;
    }
}
