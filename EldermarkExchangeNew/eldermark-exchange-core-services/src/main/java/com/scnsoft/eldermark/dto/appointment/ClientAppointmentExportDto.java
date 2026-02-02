package com.scnsoft.eldermark.dto.appointment;

import java.util.List;

public class ClientAppointmentExportDto {
    private List<ClientAppointmentExportRow> rows;

    private Integer timeZoneOffset;

    public List<ClientAppointmentExportRow> getRows() {
        return rows;
    }

    public void setRows(List<ClientAppointmentExportRow> rows) {
        this.rows = rows;
    }

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }
}
