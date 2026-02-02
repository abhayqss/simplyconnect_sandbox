package com.scnsoft.eldermark.dto.appointment;

import java.util.List;

public class ClientAppointmentExportRow {

    private String organizationName;

    private List<ClientAppointmentExportCommunityRow> communityRows;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<ClientAppointmentExportCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(List<ClientAppointmentExportCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}
