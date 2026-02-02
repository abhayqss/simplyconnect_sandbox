package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class ClientProgramsReport extends Report {

    private List<ClientProgramsRow> clientProgramsRows;

    public List<ClientProgramsRow> getClientProgramsRows() {
        return clientProgramsRows;
    }

    public void setClientProgramsRows(List<ClientProgramsRow> clientProgramsRows) {
        this.clientProgramsRows = clientProgramsRows;
    }
}
