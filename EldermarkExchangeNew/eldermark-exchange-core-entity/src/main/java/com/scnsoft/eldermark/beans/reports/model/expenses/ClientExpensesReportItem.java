package com.scnsoft.eldermark.beans.reports.model.expenses;

import java.util.LinkedList;

public class ClientExpensesReportItem {

    private String communityName;
    private LinkedList<ClientExpensesReportClientItem> clients;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public LinkedList<ClientExpensesReportClientItem> getClients() {
        return clients;
    }

    public void setClients(LinkedList<ClientExpensesReportClientItem> clients) {
        this.clients = clients;
    }
}
