package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class ServicePlanReport extends Report {

    private List<ServicePlanRow> servicePlanRowList;

    public List<ServicePlanRow> getServicePlanRowList() {
        return servicePlanRowList;
    }

    public void setServicePlanRowList(List<ServicePlanRow> servicePlanRowList) {
        this.servicePlanRowList = servicePlanRowList;
    }
}
