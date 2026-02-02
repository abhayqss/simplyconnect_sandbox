package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class DomainRow {

    private String domainName;

    private List<GoalRow> goalList;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<GoalRow> getGoalList() {
        return goalList;
    }

    public void setGoalList(List<GoalRow> goalList) {
        this.goalList = goalList;
    }
}
