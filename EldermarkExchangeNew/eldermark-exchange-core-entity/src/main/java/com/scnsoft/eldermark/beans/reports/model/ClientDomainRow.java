package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class ClientDomainRow {

    private String domainName;

    private List<ClientGoalRow> goalRows;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<ClientGoalRow> getGoalRows() {
        return goalRows;
    }

    public void setGoalRows(List<ClientGoalRow> goalRows) {
        this.goalRows = goalRows;
    }
}
