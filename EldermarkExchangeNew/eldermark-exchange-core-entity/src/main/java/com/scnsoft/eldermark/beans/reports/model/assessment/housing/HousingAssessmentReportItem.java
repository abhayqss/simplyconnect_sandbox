package com.scnsoft.eldermark.beans.reports.model.assessment.housing;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.time.Instant;
import java.util.List;

public class HousingAssessmentReportItem extends Report {

    private String clientCommunityName;
    private List<HousingAssessmentReportClientItem> clients;

    public String getClientCommunityName() {
        return clientCommunityName;
    }

    public void setClientCommunityName(String clientCommunityName) {
        this.clientCommunityName = clientCommunityName;
    }

    public List<HousingAssessmentReportClientItem> getClients() {
        return clients;
    }

    public void setClients(List<HousingAssessmentReportClientItem> clients) {
        this.clients = clients;
    }
}
