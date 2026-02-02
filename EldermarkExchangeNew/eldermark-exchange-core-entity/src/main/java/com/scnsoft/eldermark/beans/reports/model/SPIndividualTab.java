package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class SPIndividualTab {

    private String community;

    private List<SPIndividualClient> spIndividualClients;

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public List<SPIndividualClient> getSpIndividualClients() {
        return spIndividualClients;
    }

    public void setSpIndividualClients(List<SPIndividualClient> spIndividualClients) {
        this.spIndividualClients = spIndividualClients;
    }
}
