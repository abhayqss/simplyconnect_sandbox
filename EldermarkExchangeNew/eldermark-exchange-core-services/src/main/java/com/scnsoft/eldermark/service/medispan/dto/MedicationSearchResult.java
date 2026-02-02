package com.scnsoft.eldermark.service.medispan.dto;

import java.util.List;

public class MedicationSearchResult {

    private String mediSpanId;
    private String name;
    private String route;
    private String doseForm;
    private String strength;
    private String gpi;

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

    public String getDoseForm() {
        return doseForm;
    }

    public void setDoseForm(String doseForm) {
        this.doseForm = doseForm;
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

    public String getGpi() {
        return gpi;
    }

    public void setGpi(String gpi) {
        this.gpi = gpi;
    }
}
