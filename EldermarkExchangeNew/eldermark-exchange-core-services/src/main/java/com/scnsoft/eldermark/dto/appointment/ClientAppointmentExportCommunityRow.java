package com.scnsoft.eldermark.dto.appointment;

import java.util.List;

public class ClientAppointmentExportCommunityRow {
    private String communityName;

    private List<ClientAppointmentExportClientRow> clientRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public List<ClientAppointmentExportClientRow> getClientRows() {
        return clientRows;
    }

    public void setClientRows(List<ClientAppointmentExportClientRow> clientRows) {
        this.clientRows = clientRows;
    }
}
