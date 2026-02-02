package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class TimeToCompleteReport extends Report {

    private List<TimeToCompleteResult> timeToCompleteResultList;

    public List<TimeToCompleteResult> getTimeToCompleteResultList() {
        return timeToCompleteResultList;
    }

    public void setTimeToCompleteResultList(List<TimeToCompleteResult> timeToCompleteResultList) {
        this.timeToCompleteResultList = timeToCompleteResultList;
    }

}
