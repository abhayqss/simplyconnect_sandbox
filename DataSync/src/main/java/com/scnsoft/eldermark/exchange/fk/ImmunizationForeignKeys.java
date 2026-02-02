package com.scnsoft.eldermark.exchange.fk;

public class ImmunizationForeignKeys {
    private Long residentId;
    private Long vaccineId;
    private Long routeId;
    private Long injectionSiteId;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(Long vaccineId) {
        this.vaccineId = vaccineId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getInjectionSiteId() {
        return injectionSiteId;
    }

    public void setInjectionSiteId(Long injectionSiteId) {
        this.injectionSiteId = injectionSiteId;
    }
}
