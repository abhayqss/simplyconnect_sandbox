package com.scnsoft.eldermark.entity.lab.report;

import java.util.List;

public class LabResearchOrderReportListItem extends LabResearchOrderResultWithClient{
    private List<LabResearchOrderResultCodeValue> results;

    public LabResearchOrderReportListItem(LabResearchOrderResultWithClient orderWithClient, List<LabResearchOrderResultCodeValue> results) {
        super(orderWithClient.getId(), orderWithClient.getReason(), orderWithClient.getSpecimenDate(), orderWithClient.getClientId(),
                orderWithClient.getClientFirstName(), orderWithClient.getClientLastName(), orderWithClient.getClientCommunityName(), orderWithClient.getOruReceivedDatetime());
        this.results = results;
    }

    public List<LabResearchOrderResultCodeValue> getResults() {
        return results;
    }

    public void setResults(List<LabResearchOrderResultCodeValue> results) {
        this.results = results;
    }
}
