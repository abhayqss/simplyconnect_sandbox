package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class ClientServicesReport extends Report {

    List<ClientServicePlanRow> servicePlanRows;

    public List<ClientServicePlanRow> getServicePlanRows() {
        return servicePlanRows;
    }

    public void setServicePlanRows(List<ClientServicePlanRow> servicePlanRows) {
        this.servicePlanRows = servicePlanRows;
    }
}
